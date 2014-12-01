package com.cctintl.c3dfx.demos.gui.sidemenu;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;

import javax.annotation.PostConstruct;

import com.cctintl.c3dfx.controls.C3DListView;
import com.cctintl.c3dfx.demos.gui.uicomponents.ButtonController;
import com.cctintl.c3dfx.demos.gui.uicomponents.CheckboxController;
import com.cctintl.c3dfx.demos.gui.uicomponents.DialogController;
import com.cctintl.c3dfx.demos.gui.uicomponents.IconsController;
import com.cctintl.c3dfx.demos.gui.uicomponents.ListViewController;
import com.cctintl.c3dfx.demos.gui.uicomponents.PopupController;
import com.cctintl.c3dfx.demos.gui.uicomponents.ProgressBarController;
import com.cctintl.c3dfx.demos.gui.uicomponents.RadioButtonController;
import com.cctintl.c3dfx.demos.gui.uicomponents.SliderController;
import com.cctintl.c3dfx.demos.gui.uicomponents.SpinnerController;
import com.cctintl.c3dfx.demos.gui.uicomponents.TextFieldController;
import com.cctintl.c3dfx.demos.gui.uicomponents.ToggleButtonController;

@FXMLController(value = "/resources/fxml/SideMenu.fxml", title = "Material Design Example")
public class SideMenuController {

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@FXML
	@ActionTrigger("buttons")
	private Label button;

	@FXML
	@ActionTrigger("checkbox")
	private Label checkbox;

	@FXML
	@ActionTrigger("dialogs")
	private Label dialogs;

	@FXML
	@ActionTrigger("icons")
	private Label icons;

	@FXML
	@ActionTrigger("listview")
	private Label listview;

	@FXML
	@ActionTrigger("progressbar")
	private Label progressbar;

	@FXML
	@ActionTrigger("radiobutton")
	private Label radiobutton;

	@FXML
	@ActionTrigger("slider")
	private Label slider;

	@FXML
	@ActionTrigger("spinner")
	private Label spinner;

	@FXML
	@ActionTrigger("textfield")
	private Label textfield;

	@FXML
	@ActionTrigger("togglebutton")
	private Label togglebutton;

	@FXML
	@ActionTrigger("popup")
	private Label popup;
	
	@FXML
	private C3DListView<?> sideList;

	@PostConstruct
	public void init() throws FlowException, VetoException {
		sideList.propagateMouseEventsToParent();
		FlowHandler contentFlowHandler = (FlowHandler) context.getRegisteredObject("ContentFlowHandler");
		Flow contentFlow = (Flow) context.getRegisteredObject("ContentFlow");
		bindNodeToController(button, ButtonController.class, contentFlow, contentFlowHandler);
		bindNodeToController(checkbox, CheckboxController.class, contentFlow, contentFlowHandler);
		bindNodeToController(dialogs, DialogController.class, contentFlow, contentFlowHandler);
		bindNodeToController(icons, IconsController.class, contentFlow, contentFlowHandler);
		bindNodeToController(listview, ListViewController.class, contentFlow, contentFlowHandler);
		bindNodeToController(progressbar, ProgressBarController.class, contentFlow, contentFlowHandler);
		bindNodeToController(radiobutton, RadioButtonController.class, contentFlow, contentFlowHandler);
		bindNodeToController(slider, SliderController.class, contentFlow, contentFlowHandler);
		bindNodeToController(spinner, SpinnerController.class, contentFlow, contentFlowHandler);
		bindNodeToController(textfield, TextFieldController.class, contentFlow, contentFlowHandler);
		bindNodeToController(togglebutton, ToggleButtonController.class, contentFlow, contentFlowHandler);
		bindNodeToController(popup, PopupController.class, contentFlow, contentFlowHandler);
	}

	private void bindNodeToController(Node node, Class<?> controllerClass, Flow flow, FlowHandler flowHandler) {
		flow.withGlobalLink(node.getId(), controllerClass);
		node.setOnMouseClicked((e) -> {
			try {
				flowHandler.handle(node.getId());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
	}

}