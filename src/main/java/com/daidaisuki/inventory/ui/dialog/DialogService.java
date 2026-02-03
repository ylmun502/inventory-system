package com.daidaisuki.inventory.ui.dialog;

import com.daidaisuki.inventory.base.controller.BaseDialogController;
import com.daidaisuki.inventory.interfaces.FxmlView;
import com.daidaisuki.inventory.util.ViewLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;

public class DialogService {
  private final Window owner;

  public DialogService(Window owner) {
    this.owner = owner;
  }

  public <R, C extends BaseDialogController<R, ?>> R showDialog(
      Class<C> controllerClass, FxmlView view, Object... viewModels) {
    try {
      Pair<Parent, C> pair =
          ViewLoader.loadViewWithControllerFactory(
              view, type -> ViewLoader.createControllerInstance(type, viewModels));
      C controller = pair.getValue();
      Stage dialogStage = new Stage();
      dialogStage.initModality(Modality.APPLICATION_MODAL);
      dialogStage.initOwner(owner);
      dialogStage.setScene(new Scene(pair.getKey()));
      dialogStage.setTitle(view.toString().replace("_", " "));
      controller.setDialogStage(dialogStage);
      dialogStage.showAndWait();
      return controller.getResult();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
