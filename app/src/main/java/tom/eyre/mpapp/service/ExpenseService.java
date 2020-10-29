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
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import tom.eyre.yourvotematters.data.ExpenseByYear;
import tom.eyre.yourvotematters.data.ExpenseCalculatedResponse;
import tom.eyre.yourvotematters.data.ExpenseResponse;
import tom.eyre.yourvotematters.data.ExpenseType;
import tom.eyre.yourvotematters.util.GetCsvUtil;
import tom.eyre.yourvotematters.util.GitUtil;

public class ExpenseService {

    private static final String URL = "http://www.theipsa.org.uk";
    private static final String COSTS = "/mp-costs/your-mp/";

    private static final String MP_COST_ID_SEARCH = "\"MpSummaries\":[{\"Id\":";

    @SneakyThrows
    public JSONArray getMembersOfParliament(String username, String password, String name) {
        String firstName = name.split(" ")[name.split(" ").length-2];
        String lastName = name.split(" ")[name.split(" ").length-1];
        String json = checkGit(username,password,firstName + "-" + lastName);
        if(!json.equalsIgnoreCase("error") && !json.equalsIgnoreCase("fnf")) return new JSONArray(json);
        Integer costsId = getCostsId(URL + COSTS + firstName + "-" + lastName);
        if(costsId == -1){return new JSONArray();}
        String url = "https://www.theipsa.org.uk/download/downloadclaimscsvformp/" + costsId;
        System.out.println(url);
        JSONArray jsonArray = getJsonFromCsv(new GetCsvUtil().getJSONFromUrl(url));
        new GitUtil().storeFile(jsonArray.toString(), firstName + "-" + lastName);
        return jsonArray;
    }

    private String checkGit(String username, String password, String name){
        return new GitUtil().getFile(username, password,name + ".json");
    }

    public String calculateExpensesPerYear(String username, String password, String name) throws JsonProcessingException {
        JSONArray jsonArray = getMembersOfParliament(username,password,name);
        try {
            for(int i = jsonArray.length() - 1; i > 0; i--){
                if(jsonArray.getJSONObject(i).getString("Year").equalsIgnoreCase("")) {jsonArray.remove(i);}
            }
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<ExpenseResponse> expenseResponses = mapper.readValue(jsonArray.toString(), new TypeReference<ArrayList<ExpenseResponse>>() { });
            return new JSONObject(mapper.writeValueAsString(calculateExpenses(expenseResponses))).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
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

        int retries = 3;
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
