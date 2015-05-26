import com.github.jreddit.entity.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by razrs on 5/23/2015.
 */
public class SidebarUpdater {
    User user;

    public SidebarUpdater(User tempUser) {
        user = tempUser;
    }

    public void updateSidebar(String[][] commentInformation, String[][] currentCommentInformation, String subreddit, ArrayList<ArrayList<String>> valueArray) throws IOException {

        ArrayList<String> jsonElementName = valueArray.get(0);
        ArrayList<String> jsonElementValue = valueArray.get(1);

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("https://www.reddit.com/api/site_admin");
        MultipartEntity nvps = new MultipartEntity();
        httpPost.setHeader("User-Agent", "User-Agent: LGG Bot (by /u/amdphenom");
        httpPost.addHeader("Cookie", "reddit_session=" + user.getCookie());
        //nvps.addPart("r", new StringBody(subreddit));
        nvps.addPart("uh", new StringBody(user.getModhash()));
        nvps.addPart("collapse_deleted_comments", new StringBody(jsonElementValue.get(19)));
        nvps.addPart("lang", new StringBody("en_US"));
        nvps.addPart("comment_score_hide_mins", new StringBody(jsonElementValue.get(17)));
        nvps.addPart("captcha", new StringBody("false"));
        nvps.addPart("iden", new StringBody("false"));
        nvps.addPart("exclude_banned_modqueue", new StringBody(jsonElementValue.get(14)));
        nvps.addPart("header-title", new StringBody(jsonElementValue.get(12)));
        nvps.addPart("hide_ads", new StringBody(jsonElementValue.get(26)));
        nvps.addPart("over_18", new StringBody(jsonElementValue.get(3)));
        nvps.addPart("public_traffic", new StringBody(jsonElementValue.get(4)));
        nvps.addPart("public_description", new StringBody(jsonElementValue.get(1)));
        nvps.addPart("show_media", new StringBody(jsonElementValue.get(15)));
        nvps.addPart("spam_comments", new StringBody(jsonElementValue.get(10)));
        nvps.addPart("spam_selfposts", new StringBody(jsonElementValue.get(11)));
        nvps.addPart("spam_links", new StringBody(jsonElementValue.get(0)));
        nvps.addPart("submit_text_label", new StringBody(jsonElementValue.get(8)));
        nvps.addPart("submit_text", new StringBody(jsonElementValue.get(2)));
        nvps.addPart("submit_link_label", new StringBody(jsonElementValue.get(23)));
        nvps.addPart("title", new StringBody(jsonElementValue.get(7)));
        nvps.addPart("wiki_edit_age", new StringBody(jsonElementValue.get(13)));
        nvps.addPart("wiki_edit_karma", new StringBody(jsonElementValue.get(22)));
        //nvps.addPart("lang", new StringBody(jsonElementValue.get(6)));
        nvps.addPart("wikimode", new StringBody(jsonElementValue.get(25)));
        nvps.addPart("api_type", new StringBody("json"));
        nvps.addPart("css_on_cname", new StringBody("true"));
        nvps.addPart("link_type", new StringBody(jsonElementValue.get(21)));
        nvps.addPart("name", new StringBody(jsonElementValue.get(7)));
        nvps.addPart("show_cname_sidebar", new StringBody("true"));
        nvps.addPart("sr", new StringBody(subreddit));
        nvps.addPart("suggested_comment_sort", new StringBody("none"));
        nvps.addPart("type", new StringBody("public"));
        String descriptionRename = jsonElementValue.get(5);
        descriptionRename = descriptionRename.replaceFirst("Homescreen of the Week]\\([A-z0-9:\\/.]*\\)\\n#### \\/u\\/[a-zA-Z1-9]* with [0-9]*",
                "Homescreen of the Week](http://reddit.com/r/" + subreddit + "/" + currentCommentInformation[1][3] + ")\n" + "#### \\/u\\/"
                        + currentCommentInformation[0][1]
                        + " with "
                        + currentCommentInformation[0][2]);
        descriptionRename = descriptionRename.replaceFirst("Photo of the Week]\\([A-z0-9:\\/.]*\\)\\n#### \\/u\\/[a-zA-Z1-9]* with [0-9]*",
                "Photo of the Week](http://reddit.com/r/" + subreddit + "/" + currentCommentInformation[1][3] + ")\n" + "#### \\/u\\/"
                        + currentCommentInformation[1][1]
                        + " with "
                        + currentCommentInformation[1][2]);
        descriptionRename = descriptionRename.replaceFirst("\\[Photo]\\([0-9A-Za-z:\\/.]*\\)\\* [^\\x00-\\x7F] \\/u\\/[A-Za-z0-9]*",
                // [^\x00-\x7F] \/u\/[A-Za-z0-9]* \*\*[0-9]* points
                "[Photo]("
                        + currentCommentInformation[1][0]
                        + ")* � /u/"
                        + currentCommentInformation[1][1]);
                        //+ " \\" + "*" + "\\" +"*"
                        //+ "\\*\\*"
                        //+ currentCommentInformation[1][2]
                        //+ " points";
        descriptionRename = descriptionRename.replaceAll("[A-z]* [0-9]* [0-9][0-9][0-9][0-9]",
                commentInformation[0][3]).replace(",","");

        descriptionRename = descriptionRename.replaceAll("[0-9]* [A-z]* [0-9][0-9][0-9][0-9]",
                          commentInformation[0][3].replace(",","").replaceAll(" [A-Za-z]* ", "").replaceAll("[0-9][0-9][0-9][0-9]", "")
                        + commentInformation[0][3].replace(",","").replaceAll("[A-z]* [0-9]* ",""));

        descriptionRename = descriptionRename.replaceFirst("\\[Homescreen]\\([0-9A-Za-z:\\/.]*\\)\\* [^\\x00-\\x7F] \\/u\\/[A-Za-z0-9]*",
                // [^\x00-\x7F] \/u\/[A-Za-z0-9]* \*\*[0-9]* points
                "[Homescreen]("
                        + currentCommentInformation[0][0]
                        + ")* � /u/"
                        + currentCommentInformation[0][1]);
                //+ "\\\\*\\\\*"
                //+ currentCommentInformation[0][2]
                //+ " points";
        descriptionRename = descriptionRename.replaceAll("amp;","");

        nvps.addPart("description", new StringBody(descriptionRename));
        httpPost.setEntity(nvps);
        System.out.println(httpPost.toString());
        CloseableHttpResponse response2 = httpclient.execute(httpPost);
        try {
            System.out.println(response2.getStatusLine());
            HttpEntity entity2 = response2.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity2);
        } finally {
            response2.close();
        }
    }

    private String getSideBar(String subreddit) throws IOException {
        HttpClient client = new DefaultHttpClient();
        URL url = null;
        try {
            url = new URL("http://www.reddit.com/r/" + subreddit + "/about/edit.json");
            HttpGet httpGet = new HttpGet(String.valueOf(url));
            client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, System.getProperty("User-Agent: LGG Bot (by /u/amdphenom"));
            httpGet.addHeader("Cookie", "reddit_session=" + user.getCookie());
            httpGet.addHeader("uh", user.getModhash());

            HttpResponse response = client.execute(httpGet);

            HttpEntity ht = response.getEntity();

            BufferedHttpEntity buf = new BufferedHttpEntity(ht);

            InputStream is = buf.getContent();

            BufferedReader r = new BufferedReader(new InputStreamReader(is));

            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            //System.out.println(total.toString());
            return total.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ("fail");
    }


    public ArrayList<ArrayList<String>> getSubredditInfo(String subreddit) throws IOException, ParseException {

        JSONObject obj = new JSONObject(getSideBar(subreddit));
        ArrayList<String> jsonElementName = new ArrayList();
        ArrayList<String> jsonElementValue = new ArrayList();
        obj = obj.getJSONObject("data");
        String[] elementNames = JSONObject.getNames(obj);

        for (int i = 0; i < obj.length(); i++) {
            jsonElementName.add(elementNames[i]);
            jsonElementValue.add(obj.get(elementNames[i]).toString());
        }

        ArrayList<ArrayList<String>> tempArray = new ArrayList<ArrayList<String>>();
        tempArray.add(jsonElementName);
        tempArray.add(jsonElementValue);
        return tempArray;

    }

    private String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}