package controller.tutor;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import controller.GameController;
import controller.HeroSimulatorStage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import models.Map;
import util.properties.PropLoader;

public class TutorController extends GameController {
	
	private PropLoader prop;
	private Tutor tutor;
	private TutorI tutorCon;
	private TutorManager manager;
	private List<Integer> sentIDs; //sent ids of student
	private TutorState state;

	public TutorController(HeroSimulatorStage sim) {
		super(sim);
		this.prop = super.sim.getPropLoader();
		this.manager = new TutorManager(Integer.parseInt(prop.getPort()));
		this.setState(TutorState.NOTHING_SENT);
		
		if(prop.getRole().equals("student")) {
			this.sentIDs = new LinkedList<Integer>();
			this.tutorCon = this.manager.getTutorAsClient();
		} else if(prop.getRole().equals("tutor")) {
			this.tutor = this.manager.getTutorAsServer();
		}
	}
	
	public synchronized void setState(TutorState s) {
		this.state = s;
		switch(state.name()) {
		case("REQUEST_SENT"): 
			super.sim.getTutItem2().setDisable(false);
			break;
		case("NOTHING_SENT"):
			super.sim.getTutItem2().setDisable(true);
			if(prop.getRole().equals("tutor")) super.sim.getTutItem1().setDisable(false);
			break;
		case("REQUEST_GOT"):
			super.sim.getTutItem1().setDisable(true);
			super.sim.getTutItem2().setDisable(false);
			break;
		}
	}
	
	public TutorState getState() {
		return this.state;
	}


	public synchronized void studentGetAnswer() {
		if(tutorCon == null) {
			Alert alert = new Alert(AlertType.INFORMATION, super.sim.getLanguageController().getRes().getString("notutorfound"), ButtonType.CANCEL);
			alert.showAndWait();
			return;
		}
		try {
			TransferObject t_object = this.tutorCon.getAnswer(this.sentIDs.get(0));
			if(t_object == null) {
				Alert alert = new Alert(AlertType.INFORMATION, super.sim.getLanguageController().getRes().getString("notutoranswer"), ButtonType.CANCEL);
				alert.showAndWait();
				return;
			}
			
			super.sim.getEditor().setText(t_object.getCode());
			super.sim.getMapController().getMap().deserializeMap(t_object.getMap());
			this.sentIDs.remove(0);
			if(sentIDs.size() < 1)	this.setState(TutorState.NOTHING_SENT);
		} catch (RemoteException e) {
			Alert alert = new Alert(AlertType.INFORMATION, super.sim.getLanguageController().getRes().getString("notutorfound"), ButtonType.CANCEL);
			alert.showAndWait();
		}
		
	}
	
	public synchronized void studentSendRequest() {
		if(tutorCon == null) {
			Alert alert = new Alert(AlertType.INFORMATION, super.sim.getLanguageController().getRes().getString("notutorfound"), ButtonType.CANCEL);
			alert.showAndWait();
			return;
		}
		try {
			Map transferMap = super.sim.getMapController().getMap();
			TransferObject t_object = new TransferObject(super.sim.getEditor().getText(), transferMap);
			t_object.setID(this.tutorCon.sendRequest(t_object));
			this.sentIDs.add(t_object.getID());
			this.setState(TutorState.REQUEST_SENT);
		} catch (RemoteException e) {
			Alert alert = new Alert(AlertType.INFORMATION, super.sim.getLanguageController().getRes().getString("notutorfound"), ButtonType.CANCEL);
			alert.showAndWait();
		}
	}
	
	public synchronized void tutorSendAnswer() {
		if(tutor == null) return;
		TransferObject buffer = this.tutor.getCurrent();
		if(buffer != null) {
			buffer.setCode(super.sim.getEditor().getText());
			buffer.setMap(super.sim.getMapController().getMap());
		}
		this.tutor.prepareAnswer();
		this.setState(TutorState.NOTHING_SENT);
	}
	
	public synchronized void tutorGetRequest() {
		if(tutor == null) return;
		TransferObject t_object = this.tutor.getNextRequest();
		if(t_object == null) {
			Alert alert = new Alert(AlertType.INFORMATION, super.sim.getLanguageController().getRes().getString("norequestsent"), ButtonType.CANCEL);
			alert.showAndWait();
			return;
		}
		
		super.sim.getEditor().setText(t_object.getCode());
		super.sim.getMapController().getMap().deserializeMap(t_object.getMap());
		this.setState(TutorState.REQUEST_GOT);
	}
	
	
	
}
