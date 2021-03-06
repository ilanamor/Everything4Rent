package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;

public class updatesecondhandController {
    public ChoiceBox condition;
    public Button regbtm;
    public TextField itemNamefld;
    public Button picfld;
    private String picname="";
    private String category="Second-hand";
    public TextField desc;
    public TextField itemID;

    ObservableList<String> q1= FXCollections.observableArrayList( "דרוש תיקון","במצב טוב","חדש");

    public void initialize() {
        condition.setItems(q1);
        condition.setValue("");

    }

    private void showAlertError(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(alertMessage);
        alert.show();
    }

    private void showAlertInfo(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void save() throws Exception {
        if (itemNamefld.getText().isEmpty() || condition.getValue().toString().isEmpty() || picname.equals("")) {
            showAlertError("לא מילאת את כל השדות");
            return;
        }
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:db.db");
        PreparedStatement prep = conn.prepareStatement("UPDATE secondHand SET name=?, picture=?, description=?, condition=? WHERE ID=?;");
        try {
            prep.setString(1, itemNamefld.getText());
            prep.setString(2,picname);
            prep.setString(3, desc.getText());
            prep.setString(4, condition.getValue().toString());
            prep.setInt(5,Integer.parseInt(itemID.getText()));

            prep.executeUpdate();

            conn.close();

        } catch (SQLException e) {
            showAlertError("ארעה שגיאה");
            conn.close();
            return;
        }

        showAlertInfo("הפריט עודכן בהצלחה");
    }

    public void uploadPicure() throws Exception {
        FileChooser fileC = new FileChooser();
        fileC.setTitle("Open Image File");
        FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("JPEG", "*.jpg");
        FileChooser.ExtensionFilter fileExtensions1 = new FileChooser.ExtensionFilter("JPEG", "*.png");
        fileC.getExtensionFilters().add(fileExtensions);
        fileC.getExtensionFilters().add(fileExtensions1);
        File f = fileC.showOpenDialog((Stage) itemNamefld.getScene().getWindow());
        if (f != null) {
            picname= f.getAbsolutePath().toString();
            return;
        }
        picname="";
    }


    public void findItem(ActionEvent actionEvent) {
        try
        {
            int item=Integer.parseInt(itemID.getText());
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:db.db");
            PreparedStatement prep = conn.prepareStatement("SELECT * FROM secondHand WHERE ID=?;");
            prep.setString(1,item+"");
            ResultSet rs=prep.executeQuery();
            if(!rs.next() || !(Main.x).equals(rs.getString(1)))
            {
                showAlertError("מספר פריט לא חוקי");
                return;
            }

            itemNamefld.setText(rs.getString(3));
            picname=rs.getString(5);
            desc.setText(rs.getString(6));
            condition.setValue(rs.getString(7));
            conn.close();
        }
        catch(NumberFormatException e)
        {
            showAlertError("מספר פריט לא חוקי");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(ActionEvent actionEvent)
    {
        try
        {
            int item=Integer.parseInt(itemID.getText());
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:db.db");
            PreparedStatement prep = conn.prepareStatement("DELETE FROM secondHand WHERE ID=?;");
            prep.setInt(1,item);
            prep.executeUpdate();
            itemNamefld.setText("");
            picname="";
            desc.setText("");
            conn.close();

            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:db.db");
            prep = conn.prepareStatement("DELETE FROM packItems WHERE itemnum=?;");
            prep.setString(1,item+"");
            prep.executeUpdate();
            conn.close();
            itemID.setText(" ");
            showAlertInfo("הפריט נמחק בהצלחה");

        }
        catch(NumberFormatException e)
        {
            showAlertError("מספר פריט לא חוקי");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
