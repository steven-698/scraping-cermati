import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;

public class Solution {
    private static FileWriter file;

    public static void main(String[] args) {
        JSONObject scraped_data = new JSONObject();
        JSONArray articlesArr = new JSONArray();
        try {
            String url = "https://www.cermati.com";
            Document page = Jsoup.connect(url + "/artikel").userAgent("Jsoup Scraper").get();

            Elements divListArticles = page.select(".list-of-articles .article-list-item a");

            for (Element item : divListArticles) {
                String urlDetail = url + item.attr("href");

                try {
                    Document detailPage = Jsoup.connect(urlDetail).userAgent("Jsoup Scraper").get();

                    JSONObject detailArticles = new JSONObject();
                    JSONArray relatedArticlesArr = new JSONArray();

                    String title = detailPage.select(".post-content .post-title").text();
                    String author = detailPage.select(".post-content .post-info .post-author .author-name").text();
                    String postingDate = detailPage.select(".post-content .post-info .post-date span").text();

                    Element divListItem = detailPage.select(".side-list-panel .panel-items-list").first();
                    Elements detailItem = divListItem.select("li a");

                    detailArticles.put("url", urlDetail);
                    detailArticles.put("title", title);
                    detailArticles.put("author", author);
                    detailArticles.put("postingDate", postingDate);

                    for (Element n : detailItem) {
                        String urlTerkait = url + n.attr("href");
                        String titleTerkait = n.select(".item-title").text();

                        JSONObject relatedArticlesItem = new JSONObject();
                        relatedArticlesItem.put("url", urlTerkait);
                        relatedArticlesItem.put("title", titleTerkait);
                        relatedArticlesArr.put(relatedArticlesItem);

                    }

                    detailArticles.put("relatedArticles", relatedArticlesArr);

                    articlesArr.put(detailArticles);


                    System.out.println();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        scraped_data.put("articles", articlesArr);
        System.out.println("\n JSON: " + scraped_data);
        saveResult(scraped_data);
    }


    public static void saveResult(JSONObject scraped_data){
        try (FileWriter file = new FileWriter("Solution.json")) {

            file.write(String.valueOf(scraped_data));
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
