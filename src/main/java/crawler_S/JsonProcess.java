package crawler_S;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.JsonBase;
import opt.RAMMD5Dedutor;
import org.apache.http.impl.client.CloseableHttpClient;
import model.JsonBase;

import java.sql.SQLException;
import java.util.List;

public class JsonProcess {
    private static int webId = 0;
    private static List<String> dataParamList = null;
    static int fDataNum = 0;
    static int sDataNum = 0;
    public static CloseableHttpClient client = null;
    public static String cookies = null;

    public static int SampleData_sum=0;

    public JsonProcess(int webId) {
        this.webId = webId;
    }

    static void json_process(int webId, String html, RAMMD5Dedutor dedu) {
        Gson g = new Gson();
        JsonBase jsonbase = (JsonBase) DBUtil.selectMutilpara("jsonBase", new String[]{"*"}, webId, JsonBase.class).get(0);
        JsonObject data = g.fromJson(html, JsonObject.class);
        JsonArray jsonArray = null;
        if (jsonbase.getcontentAddress() != null && jsonbase.getcontentAddress().length() > 0) {
            String[] contentAddress = jsonbase.getcontentAddress().split("/");
            for (int i = 1; i < contentAddress.length - 1; i++) {
                data = data.getAsJsonObject(contentAddress[i]);
            }
            jsonArray = data.getAsJsonArray(contentAddress[contentAddress.length - 1]);
        } else jsonArray = data.getAsJsonArray();
        int alldata = jsonArray.size();
        for (int i = 0; i < alldata; i++) {
            if (!dedu.add(jsonArray.get(i).toString()))
                jsonArray.remove(i);
        }
        int dataSize = jsonArray.size();
//        System.out.println("dataSize is "+dataSize);

        try {
            exit(dataSize, alldata - dataSize, webId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void exit(int sNum, int fNum, int webId) throws SQLException {
        fDataNum += fNum;
        sDataNum += sNum;
        SampleData_sum = SampleData_sum + sNum;
        System.out.print("SampleData_sum   ");
        System.out.println(SampleData_sum);
    }

    public static void reset() {

        fDataNum = 0;
        sDataNum = 0;
    }



    public static void main(String[] args) {

    }

}
