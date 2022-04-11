package hu.bme.mit.yakindu.analysis.workhere;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		State previous = null;
		int nevtelen = 0;
		
		TreeIterator<EObject> iterator = s.eAllContents();
		//4.4. exercise
		List <String> variables = new LinkedList();
		List <String> events = new LinkedList();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				State state = (State) content;
				System.out.println(state.getName());
				//2.3. exercise
				EList<Transition> transitions = state.getOutgoingTransitions();
				
				for(int i = 0 ; i < transitions.size(); i++) {
					System.out.println(transitions.get(i).getSource().getName() + " -> " + transitions.get(i).getTarget().getName());
				}
				
				
				//if(previous != null) {
				//	System.out.println(previous.getName() + " -> " + state.getName());
				//}
				previous = state;
				//2.4. exercise
				if(state.getOutgoingTransitions().size() == 0) {
					System.out.println("Csapda: " + state.getName());
				}
				//2.5. exercise
				if(state.getName().isEmpty() == true) {
					System.out.println("Névtelen állapot! Ajánlott név: State" + nevtelen);
					nevtelen++;
				}
			}
			//4.3. exercise
			if(content instanceof EventDefinition) {
				EventDefinition ed =  (EventDefinition) content;
				//System.out.println("Event: " + ed.getName());
				//for 4.4.
				events.add(ed.getName());
			}
			if(content instanceof VariableDefinition) {
				VariableDefinition vd = (VariableDefinition) content;
				//System.out.println("Variable: " + vd.getName());
				//for 4.4.
				variables.add(vd.getName());
			}
		}
		
		//4.4. exercise
		System.out.println("public static void print(IExampleStatemachine s) {");
		for(int i = 0; i < events.size(); i++) {
			String tmp = events.get(i);
			String ev = tmp.substring(0,1).toUpperCase() + tmp.substring(1);
			char firstcharacter = ev.charAt(0);
			System.out.println("System.out.println(\"" + firstcharacter + " = \" + s.getSCIntergace().get" + ev + "());");
		}
		for(int i = 0; i < variables.size(); i++) {
			String tmp = variables.get(i);
			String var = tmp.substring(0,1).toUpperCase() + tmp.substring(1);
			char firstcharacter = var.charAt(0);
			System.out.println("System.out.println(\"" + firstcharacter + " = \" + s.getSCIntergace().get" + var + "());");
		}
		System.out.println("}");
		
		//4.5. exercise
		System.out.println("public static void main(String[] args) throws IOException {\n" +
		"   ExampleStatemachine s = new ExampleStatemachine();\n" +
		"   s.setTimer(new TimerService());\n" +
		"   RuntimeService.getInstance().registerStatemachine(s, 200);\n" +
		"   s.init();\n" +
		"   s.enter();\n" +
		"   s.runCycle();\n" + 
		"   \n" + 
		"   BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));\n" +
		"   String input = reader.readLine();\n" +
		"   while(!input.equals(\"exit\")) {");
		for(int i = 0; i < events.size(); i++) {
			String tmp = events.get(i);
			String ev = tmp.substring(0,1).toUpperCase() + tmp.substring(1);
			System.out.println("      if(input.equals(\""+tmp+"\")) {\n" + 
					"         s.raise" + ev + "(); \n" +
					"         s.runCycle();\n" +
					"      }");
		}
		System.out.println("      print(s)\n" +
		"      input = reader.readLine();\n" +
		"   }\n" +
		"   System.exit(0); \n" +
		"}");
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
