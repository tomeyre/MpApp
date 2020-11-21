package tom.eyre.mpapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import lombok.SneakyThrows;
import tom.eyre.mpapp.data.ExpenseByYear;
import tom.eyre.mpapp.data.ExpenseCalculatedResponse;
import tom.eyre.mpapp.data.ExpenseResponse;
import tom.eyre.mpapp.data.ExpenseType;
import tom.eyre.mpapp.database.MpDatabase;
import tom.eyre.mpapp.entity.ExpenseEntity;
import tom.eyre.mpapp.entity.MpEntity;
import tom.eyre.mpapp.util.GetCsvUtil;
import tom.eyre.mpapp.util.GitUtil;

import static tom.eyre.mpapp.util.Constants.SEVEN_DAYS;

public class ExpenseService {

    private static final Logger log = Logger.getLogger(String.valueOf(ExpenseService.class));
    private static final String URL = "http://www.theipsa.org.uk";
    private static final String COSTS = "/mp-costs/your-mp/";

    private static final String MP_COST_ID_SEARCH = "\"MpSummaries\":[{\"Id\":";

    @SneakyThrows
    public JSONArray getMembersOfParliament(MpEntity mp) {
        Integer costsId = getCostsId(URL + COSTS + mp.getForename() + "-" + mp.getSurname());
        if(costsId == -1){costsId = getCostsId(URL + COSTS + mp.getFullName().trim().replaceAll(" +", " ").replaceAll(" ", "-"));}
        if(costsId == -1){return new JSONArray();}
        String url = "https://www.theipsa.org.uk/download/downloadclaimscsvformp/" + costsId;
        log.info(url);
        JSONArray jsonArray = getJsonFromCsv(new GetCsvUtil().getJSONFromUrl(url));

        return jsonArray;
    }

    private String checkGit(String username, String password, String name){
        return new GitUtil().getFile(username, password,name + ".json");
    }

    public ExpenseCalculatedResponse calculateExpensesPerYear(MpEntity mp, MpDatabase mpDatabase) throws JsonProcessingException {
        if(haveCurrentExpenses(mp.getId(), mpDatabase)) return calculateExpenseEntities((ArrayList<ExpenseEntity>) mpDatabase.mpDao().getExpensesByMpId(mp.getId()));
        JSONArray jsonArray = getMembersOfParliament(mp);
        try {
            for(int i = jsonArray.length() - 1; i > 0; i--){
                if(jsonArray.getJSONObject(i).getString("Year").equalsIgnoreCase("")) {jsonArray.remove(i);}
            }
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<ExpenseResponse> expenseResponses = mapper.readValue(jsonArray.toString(), new TypeReference<ArrayList<ExpenseResponse>>() { });
            buildAndSaveEntities(expenseResponses,mpDatabase,mp.getId());
            return calculateExpenses(expenseResponses);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Boolean haveCurrentExpenses(Integer id, MpDatabase mpDatabase){
        Long expenseDate = mpDatabase.mpDao().getExpenseDate(id);
        return expenseDate != null ? System.currentTimeMillis() - expenseDate < SEVEN_DAYS : false;
    }

    private ExpenseCalculatedResponse calculateExpenseEntities(ArrayList<ExpenseEntity> expenses){
        ExpenseCalculatedResponse response = new ExpenseCalculatedResponse();
        for(ExpenseEntity expense : expenses){
            if(responseContainsExpenseYearAlready(response, expense)){
                for(ExpenseByYear expenseByYear : response.getExpenseByYears()){
                    if(expenseByYear.getYear().equalsIgnoreCase(expense.getYear() instanceof String ? expense.getYear() : "")){
                        if(expenseYearContainsExpenseTypeAlready(expenseByYear.getExpenseTypes(),expense.getExpenseType())){
                            for(ExpenseType type : expenseByYear.getExpenseTypes()){
                                if(type.getType().equalsIgnoreCase(expense.getExpenseType())){
                                    type.setTotalSpent(type.getTotalSpent() + (expense.getAmountClaimed() - (expense.getAmountNotPaid() != null ? expense.getAmountNotPaid() : 0d)));
                                }
                            }
                        }else{
                            expenseByYear.getExpenseTypes().add(new ExpenseType(expense.getExpenseType(), (expense.getAmountClaimed() - (expense.getAmountNotPaid() != null ? expense.getAmountNotPaid() : 0d))));
                        }
                    }
                }
            }else{
                List<ExpenseType> expenseTypes = new ArrayList<>();
                expenseTypes.add(new ExpenseType(expense.getExpenseType(), (expense.getAmountClaimed() - (expense.getAmountNotPaid() != null ? expense.getAmountNotPaid() : 0d))));
                response.getExpenseByYears().add(new ExpenseByYear(expense.getYear() instanceof String ? expense.getYear() : "", expenseTypes));
            }
        }

        return response;
    }

    private void buildAndSaveEntities(ArrayList<ExpenseResponse> responses, MpDatabase mpDatabase, Integer id){
        ArrayList<ExpenseEntity> expenseEntities = new ArrayList<>();
        for(ExpenseResponse response : responses){
            ExpenseEntity expenseEntity = new ExpenseEntity();
            expenseEntity.setMpId(id);
            expenseEntity.setYear(response.getYear() instanceof String ? response.getYear().toString() : "");
            expenseEntity.setDate(response.getDate());
            expenseEntity.setClaimNo(response.getClaimNo());
            expenseEntity.setMpName(response.getMpsName());
            expenseEntity.setMpsConstituency(response.getMpsConstituency());
            expenseEntity.setCategory(response.getCategory());
            expenseEntity.setExpenseType(response.getExpenseType());
            expenseEntity.setShortDescription(response.getShortDescription());
            expenseEntity.setDetails(response.getDetails());
            expenseEntity.setJourneyType(response.getJourneyType());
            expenseEntity.setFrom(response.getTraveledFrom());
            expenseEntity.setTo(response.getTraveledTo());
            expenseEntity.setTravel(response.getTravelClass());
            expenseEntity.setNights(response.getNights());
            expenseEntity.setMileage(response.getMileage());
            expenseEntity.setAmountClaimed(response.getAmountClaimed());
            expenseEntity.setAmountPaid(response.getAmountPaid());
            expenseEntity.setAmountRepaid(response.getAmountRepaid());
            expenseEntity.setStatus(response.getStatus());
            expenseEntity.setReasonIfNotPaid(response.getReasonIfNotPaid());
            expenseEntity.setSupplyMonth(response.getSupplyMonth());
            expenseEntity.setSupplyPeriod(response.getSupplyPeriod());
            expenseEntity.setLastUpdatedTimestamp(new Date().getTime());
            expenseEntities.add(expenseEntity);
        }
        mpDatabase.mpDao().insertAllExpenses(expenseEntities);
    }

    private ExpenseCalculatedResponse calculateExpenses(ArrayList<ExpenseResponse> expenses){
        ExpenseCalculatedResponse response = new ExpenseCalculatedResponse();
        for(ExpenseResponse expense : expenses){
            if(responseContainsExpenseYearAlready(response, expense)){
                for(ExpenseByYear expenseByYear : response.getExpenseByYears()){
                    if(expenseByYear.getYear().equalsIgnoreCase(expense.getYear() instanceof String ? expense.getYear().toString() : "")){
                        if(expenseYearContainsExpenseTypeAlready(expenseByYear.getExpenseTypes(),expense.getExpenseType())){
                            for(ExpenseType type : expenseByYear.getExpenseTypes()){
                                if(type.getType().equalsIgnoreCase(expense.getExpenseType())){
                                    type.setTotalSpent(type.getTotalSpent() + (expense.getAmountClaimed() - expense.getAmountNotPaid()));
                                }
                            }
                        }else{
                            expenseByYear.getExpenseTypes().add(new ExpenseType(expense.getExpenseType(), (expense.getAmountClaimed() - expense.getAmountNotPaid())));
                        }
                    }
                }
            }else{
                List<ExpenseType> expenseTypes = new ArrayList<>();
                expenseTypes.add(new ExpenseType(expense.getExpenseType(), (expense.getAmountClaimed() - expense.getAmountNotPaid())));
                response.getExpenseByYears().add(new ExpenseByYear(expense.getYear() instanceof String ? expense.getYear().toString() : "", expenseTypes));
            }
        }

        return response;
    }

    private Boolean expenseYearContainsExpenseTypeAlready(List<ExpenseType> types, String typeString){
        for(ExpenseType type : types){
            if(type.getType().equalsIgnoreCase(typeString)){
                return true;
            }
        }
        return false;
    }

    private Boolean responseContainsExpenseYearAlready(ExpenseCalculatedResponse response, ExpenseEntity expense){
        for(ExpenseByYear expenseByYear : response.getExpenseByYears()){
            if(expenseByYear.getYear() != null &&
                    expense.getYear() != null &&
                    expenseByYear.getYear().equalsIgnoreCase(expense.getYear() instanceof String ? expense.getYear().toString() : "")){
                return true;
            }
        }
        return false;
    }


    private Boolean responseContainsExpenseYearAlready(ExpenseCalculatedResponse response, ExpenseResponse expense){
        for(ExpenseByYear expenseByYear : response.getExpenseByYears()){
            if(expenseByYear.getYear() != null &&
            expense.getYear() != null &&
            expenseByYear.getYear().equalsIgnoreCase(expense.getYear() instanceof String ? expense.getYear().toString() : "")){
                return true;
            }
        }
        return false;
    }

    private JSONArray getJsonFromCsv(String csvString){
        try {
            CsvSchema csv = CsvSchema.emptySchema().withHeader();
            CsvMapper csvMapper = new CsvMapper();
            MappingIterator<Map<?, ?>> mappingIterator =  csvMapper.reader().forType(Map.class).with(csv).readValues(csvString.replaceAll("ï»¿","").replaceAll("\\uFEFF", "").replaceAll("\\u0000",""));
            List<Map<?, ?>> list = mappingIterator.readAll();
            return new JSONArray(list);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getCostsId(String url) {

        int retries = 1;
        while (retries-- > 0) {
            try {
                Document doc = Jsoup.connect(url).timeout(90000).get();
                Element el =  doc.select("span#expenses-panel-download-databtn").next("script").first();
                int index = el.data().indexOf(MP_COST_ID_SEARCH);
                int indexEnd = el.data().indexOf(",", index);
                String id = el.data().substring(index +  MP_COST_ID_SEARCH.length(), indexEnd);
                return Integer.parseInt(id);
            } catch (IOException i) {
                i.printStackTrace();
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                // log exception?
            }
        }

        return -1;
    }
}
