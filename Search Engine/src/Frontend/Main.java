package Frontend;

//!Import
import org.json.JSONArray;
import javafx.scene.control.ScrollPane;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javafx.scene.control.Hyperlink;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Main tab for initial UI setup
        TabPane tabPane = new TabPane();
        Tab mainTab = new Tab("Google");
        mainTab.setClosable(false);
        VBox mainTabContent = new VBox(20);
        mainTabContent.setBackground(
                new Background(new BackgroundFill(Color.web("#202124"), CornerRadii.EMPTY, Insets.EMPTY)));
        mainTabContent.setAlignment(Pos.TOP_CENTER);

        // Top Google image
        Image headingImage = new Image(getClass().getResourceAsStream("/Frontend/Images/Google.png"));
        ImageView imageView = new ImageView(headingImage);
        imageView.setFitWidth(500);
        imageView.setFitHeight(170);
        mainTabContent.getChildren().addAll(imageView);

        // Input Text Field
        TextField searchInputTextField = new TextField();
        searchInputTextField.setPromptText("Search");
        searchInputTextField.setMaxWidth(550);
        searchInputTextField.setMinHeight(40);
        searchInputTextField.setPrefWidth(550);
        searchInputTextField.setMinHeight(40);
        searchInputTextField.setFont(new javafx.scene.text.Font(15));
        searchInputTextField.getStyleClass().add("search_input");
        mainTabContent.getChildren().addAll(searchInputTextField);

        // Submit Button
        Button ButtonSearch = new Button();
        ButtonSearch.setText("Google Search");
        ButtonSearch.getStyleClass().add("search_button");
        Button feelingLucky = new Button();
        feelingLucky.setText("Feeling Lucky");
        feelingLucky.getStyleClass().add("search_button");

        // Hbox for button
        HBox button = new HBox(10);
        button.getChildren().addAll(ButtonSearch, feelingLucky);
        HBox.setMargin(ButtonSearch, new Insets(0, 10, 153, 500));
        mainTabContent.getChildren().addAll(button);

        mainTab.setContent(mainTabContent);
        tabPane.getTabs().add(mainTab);

        VBox.setMargin(imageView, new Insets(130, 0, 0, 0));
        VBox.setMargin(searchInputTextField, new Insets(10, 0, 0, 0));
        VBox.setMargin(ButtonSearch, new Insets(0, 0, 120, 0));

        // Event handler for search button
        ButtonSearch.setOnAction(e -> showSearchResults(tabPane, searchInputTextField.getText()));

        String cssFile = getClass().getResource("/Frontend/Style.css").toExternalForm();
        mainTabContent.getStylesheets().add(cssFile);

        primaryStage.setTitle("Google");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Frontend/Images/logo.png")));
        primaryStage.setScene(new Scene(tabPane, 1270, 685));
        primaryStage.show();
    }

    private void showSearchResults(TabPane tabPane, String query) {
        String body;
        String search = URLEncoder.encode(query, StandardCharsets.UTF_8);
        try {
            // String apiUrl =
            // "https://www.googleapis.com/customsearch/v1?key=AIzaSyAmRBLSpWafSw-CW8G2buGGrvSvAGnKwNo&cx=017576662512468239146:omuauf_lfve&q="
            // + search;
            String apiUrl1 = "https://google-search103.p.rapidapi.com/search/?q=" + search + "&num=45&gl=us";
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl1))
                    .GET().header("X-RapidAPI-Key", "390d3da771msh870980f8383f55fp1838b9jsneee6cb7540d9")
                    .header("X-RapidAPI-Host", "google-search103.p.rapidapi.com")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            body = response.body();
            JSONObject jsonResponse = new JSONObject(body);
            if (jsonResponse.has("results")) {
                JSONArray itemsArray = jsonResponse.getJSONArray("results");
                int result = itemsArray.length();
                if (itemsArray.length() > 0) {
                    String[] titles = new String[itemsArray.length()];
                    String[] links = new String[itemsArray.length()];
                    String[] hostnames = new String[itemsArray.length()];
                    String[] descriptions = new String[itemsArray.length()];

                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject item = itemsArray.getJSONObject(i);
                        String title = item.getString("title");
                        String link = item.getString("link");
                        String hostname = item.getString("hostname");
                        String description = item.getString("description");
                        titles[i] = (title);
                        links[i] = (link);
                        hostnames[i] = (hostname);
                        descriptions[i] = (description);
                    }

                    Tab searchTab = new Tab(query);
                    VBox searchResultsContent = new VBox(20);
                    searchResultsContent.setBackground(
                            new Background(new BackgroundFill(Color.web("#202124"), CornerRadii.EMPTY, Insets.EMPTY)));
                    searchResultsContent.getChildren().add(createLabelWithWhiteText("Total results = " + result));

                    if (titles.length == 0) {
                        searchResultsContent.getChildren().add(createLabelWithWhiteText("No data found."));
                    } else {

                        for (int i = 0; i < titles.length; i++) {
                            searchResultsContent.getChildren().add(createLabelWithWhiteText(titles[i]));
                            searchResultsContent.getChildren().add(createHyperlink(links[i], links[i]));
                            searchResultsContent.getChildren().add(createLabelWithWhiteText(hostnames[i]));
                            searchResultsContent.getChildren().add(createLabelWithWhiteText(descriptions[i]));
                            searchResultsContent.getChildren().add(new Label("-------------------------------"));

                        }
                    }

                    searchResultsContent.setAlignment(Pos.TOP_LEFT);
                    VBox containerVBox = new VBox(searchResultsContent);
                    ScrollPane scrollPane = new ScrollPane(containerVBox);
                    scrollPane.setFitToWidth(true);
                    scrollPane.setFitToHeight(true);
                    searchTab.setContent(scrollPane);
                    tabPane.getTabs().add(searchTab);
                    tabPane.getSelectionModel().select(searchTab);
                }
            } else {
                System.out.println("Not That data");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Label createLabelWithWhiteText(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white;");
        label.getStyleClass().add("search_label");
        return label;
    }

    private Hyperlink createHyperlink(String url, String text) {
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.getStyleClass().add("href");
        hyperlink.setOnAction(e -> openWebpage(url));
        return hyperlink;
    }

    private void openWebpage(String url) {
        HostServices hostServices = getHostServices();
        hostServices.showDocument(url);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
