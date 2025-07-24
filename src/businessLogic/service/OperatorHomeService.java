package businessLogic.service;

import javafx.scene.image.Image;

public class OperatorHomeService {
    public Image getProfileImage() {
        var url = getClass().getResource("/businessLogic/fxml/Images/person-fill.png");
        if (url == null) {
            return new Image("https://www.gravatar.com/avatar/?d=mp&s=48");
        }
        return new Image(url.toExternalForm());
    }
}
