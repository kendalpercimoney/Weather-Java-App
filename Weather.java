import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class Weather extends JFrame {
    private JTextField cityField;
    private JLabel temperatureLabel;
    private JLabel conditionLabel;
    private JLabel humidityLabel;
    private JLabel windLabel;
    private static final String API_KEY = "-";
    
    public Weather() {
        setTitle("Weather Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        // Create panels
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        cityField = new JTextField(20);
        JButton searchButton = new JButton("Get Weather");
        searchPanel.add(new JLabel("Enter City: "));
        searchPanel.add(cityField);
        searchPanel.add(searchButton);
        
        // Weather info panel
        JPanel weatherPanel = new JPanel();
        weatherPanel.setLayout(new BoxLayout(weatherPanel, BoxLayout.Y_AXIS));
        weatherPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        temperatureLabel = new JLabel("Temperature: ");
        conditionLabel = new JLabel("Condition: ");
        humidityLabel = new JLabel("Humidity: ");
        windLabel = new JLabel("Wind: ");
        
        weatherPanel.add(temperatureLabel);
        weatherPanel.add(Box.createVerticalStrut(10));
        weatherPanel.add(conditionLabel);
        weatherPanel.add(Box.createVerticalStrut(10));
        weatherPanel.add(humidityLabel);
        weatherPanel.add(Box.createVerticalStrut(10));
        weatherPanel.add(windLabel);
        
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(weatherPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Add action listener to button
        searchButton.addActionListener(e -> {
            String city = cityField.getText().trim();
            if (!city.isEmpty()) {
                fetchWeatherData(city);
            }
        });
    }
    
    private void fetchWeatherData(String city) {
        try {
            String urlString = String.format(
                "http://api.weatherapi.com/v1/current.json?key=%s&q=%s&aqi=no",
                API_KEY,
                city
            );
            
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            );
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject current = jsonResponse.getJSONObject("current");
            
            // Update UI
            SwingUtilities.invokeLater(() -> {
                temperatureLabel.setText(String.format("Temperature: %.1f°C", 
                    current.getDouble("temp_c")));
                conditionLabel.setText("Condition: " + 
                    current.getJSONObject("condition").getString("text"));
                humidityLabel.setText("Humidity: " + 
                    current.getInt("humidity") + "%");
                windLabel.setText(String.format("Wind: %.1f km/h", 
                    current.getDouble("wind_kph")));
            });
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error fetching weather data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            Weather app = new Weather();
            app.setVisible(true);
        });
    }
}