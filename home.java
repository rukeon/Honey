e loopbackckage com.scatterlab.bxt.business.report;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;

import com.scatterlab.bxt.business.BxtService;
import com.scatterlab.bxt.business.card.util.CardServiceUtil;
import com.scatterlab.bxt.model.Message;
import com.scatterlab.bxt.util.BxtException;

public class HoneyPmmlService {
	private static Pattern heartPattern;
	
	private static Map<String, String> patterns;
	private static int MAX_TEXT_PATTERN_COUNT;
	
	static Map<String, PMML> models;
	
	@PostConstruct
	public static void init() throws Exception {
		patterns = new HashMap<>();		
		for(String readLine : BxtService.readFile("files/model/cherished_message_pmml.pattern")) {
			String data[] = readLine.split("\t");

			int count = CardServiceUtil.countSubstring(" ", data[1]);
			if(count > MAX_TEXT_PATTERN_COUNT) {
				MAX_TEXT_PATTERN_COUNT = count;
			}
			patterns.put(data[1], data[0]);
		}
		
		heartPattern = Pattern.compile("[" + patterns.entrySet().stream().filter(e -> e.getValue().equals("heart.pattern")).map(e -> e.getKey()).collect(Collectors.joining()) + "]");

//		if(new File("/scatter/bxt/cherish.pmml").exists()) {
//			System.out.println("PMML loading start");
			
//			models = new HashMap<>();
//			models.put("FEMALE.CIVILIAN", loadModel(BxtService.BXT_FILE_PATH + "/cherish/cherish.F.adult.xml"));
//			models.put("FEMALE.STUDENT", loadModel(BxtService.BXT_FILE_PATH + "/cherish/cherish.F.teen.xml"));
//			models.put("MALE.CIVILIAN", loadModel(BxtService.BXT_FILE_PATH + "/cherish/cherish.M.adult.xml"));
//			models.put("MALE.STUDENT", loadModel(BxtService.BXT_FILE_PATH + "/cherish/cherish.M.teen.xml"));
			
//			System.out.println("PMML loading done");
		}
	
//	private PMML loadModel(final String file) throws Exception {
//		PMML pmml = null;
//		try( InputStream in = new FileInputStream(new File(file))){
//			Source source = ImportFilter.apply(new InputSource(in));
//			pmml = JAXBUtil.unmarshalPMML(source);
//		} catch( Exception e) {
//			throw e;
//		}
//		return pmml;
//	}
	
	private double getScore(PMML pmml, String targetName, Map<String, Double> variables) throws BxtException {
		ModelEvaluator<?> modelEvaluator = ModelEvaluatorFactory.newInstance().newModelManager(pmml);
		Evaluator evaluator = modelEvaluator;

		Map<FieldName, FieldValue> arguments = new LinkedHashMap<FieldName, FieldValue>();
		for(FieldName activeField : evaluator.getActiveFields()){
			if(variables.containsKey(activeField.getValue()) == false) {
				throw new BxtException(BxtException.BAD_INPUT_PARAMETER_CODE, activeField.getValue());
			}
			
		    FieldValue activeValue = evaluator.prepare(activeField, evaluator.getDataField(activeField).getDataType() == DataType.STRING ? Integer.toString((int)(double)variables.get(activeField.getValue())) : variables.get(activeField.getValue()));
		    arguments.put(activeField, activeValue);
		}
		
		Map<FieldName, ?> results = evaluator.evaluate(arguments);
		return (double)results.entrySet().stream().filter(e -> e.getKey().toString().equals(targetName)).findAny().map(e -> e.getValue()).get();
	}
	
	
	public static void main( String[] args ) throws Exception {
		//init();
		List<Message> messages = YearEndReportService.parseMessages(null, "/nas1/data/K/55AK");		
		//getVariables(messages);
		
		// 이전 5개의 메시지, 먼저 각 메시지 마다 index를 만들고, index를 키로 5개의 메시지 heart, sticker를 저장	
		List<Message> recvMessage = messages.stream().filter(m -> m.getType() == Message.Type.RECV).collect(Collectors.toList());
		
		Map<Integer, Message> prevMap = new HashMap<>();
		for(int i =0; i < messages.size(); ++i)	{ // 이것 좀 8형식으로 바꿀 수 없나...
			prevMap.put(new Integer(i), messages.get(i));
		}
		
	
		
//		List<List<Message>> prevMessage5 = new ArrayList<>();
//		List<Message> prevMessage5ForOneday = messages.stream()
				
//recvMessageIndexes.subList(recvMessageIndexes.size()>3 ? recvMessageIndexes.size()-3 : 0, recvMessageIndexes.size()).stream().map(index -> oneDayMessages.get(index)).collect(Collectors.toList());
		
//		List<Integer> recvMessageIndexes = new ArrayList<>();
//		
//		for(int i=0; i< messages.size(); i++) {
//			if(messages.get(i).getType() != Message.Type.RECV) {
//				continue;
//			}
//		}

	}

//  할 것: 단어 뽑아서 doubles 로 주기! 최대한 짧게 짜봐라	
//	Map<String, Double>
	public static void  getVariables(List<Message> messages) {
//		User user = new User();
//		user.setSex(User.Sex.FEMALE);
		
		Map<String, Double> variables = new HashMap<String, Double>();
		
		for(Message message : messages) {
			System.out.println(message);
			
//			System.out.println(message.getMessageTime());
//			String date = CardServiceUtil.getProperDate(message.getMessageTime());
//			System.out.println(date); //20151021
			//variables.put("date", Double.valueOf(date));
			//variables.put("hour", Double.valueOf(message.getMessageTime().substring(8, 10)));
//			String hour = message.getMessageTime().substring(8, 10);
//			String minute = message.getMessageTime().substring(10, 12);
//			System.out.println(minute);
			
//			System.out.println(message);
//			variables.put("text.length.log", Math.log(message.getText().length() + 1));
//			variables.put("short", message.getText().length() <= 5 ? 1D : 0D);
//			variables.put("space.ratio", (double)(message.getText().length() - message.getText().replaceAll("\\s", "").length())/message.getText().length());
//			
//			variables.put("efn.ratio",(double) CardServiceUtil.countSubstring("=EFN>", message.getTextPattern()) / message.getText().length());
//			variables.put("question.detect", Pattern.compile("[/?]").matcher(message.getText()).find() ? 1D : 0D);
//			variables.put("bang.detect", Pattern.compile("[/!]").matcher(message.getText()).find() ? 1D : 0D);
//			variables.put("smile.detect", Pattern.compile("[/^]").matcher(message.getText()).find() ? 1D : 0D);
//			variables.put("tilde.detect", Pattern.compile("[/~]").matcher(message.getText()).find() ? 1D : 0D);
//			variables.put("dot.detect", Pattern.compile("[/.]").matcher(message.getText()).find() ? 1D : 0D);
//			variables.put("dot.multi.detect", Pattern.compile("(\\.\\.)").matcher(message.getText()).find() ? 1D : 0D);
//			variables.put("uw.count", (double) CardServiceUtil.countSubstring("ㅜ", message.getText()) + CardServiceUtil.countSubstring("ㅠ", message.getText()));
//			variables.put("kh.count", (double) CardServiceUtil.countSubstring("ㅋ", message.getText()) + CardServiceUtil.countSubstring("ㅎ", message.getText()));
//			variables.put("heart.detect", heartPattern.matcher(message.getText()).find() ? 1D : 0D);
////			variables.put("emoticon.detect", message.getTextPattern().indexOf("<(=SS>") >= 0 ? 1D : 0D); 
//			//emoji.detect
//			variables.put("question.ratio", (double) CardServiceUtil.countSubstring("ㅜ", message.getText()) / message.getText().length());
//			
//			double kkmaCount = CardServiceUtil.countSubstring(" ", message.getTextPattern()) + 1;
//			variables.put("ic.ratio", CardServiceUtil.countSubstring("=IC>", message.getTextPattern()) / kkmaCount);
//			variables.put("va.ratio", CardServiceUtil.countSubstring("=VA>", message.getTextPattern()) / kkmaCount);
//			variables.put("vv.ratio", CardServiceUtil.countSubstring("=VV>", message.getTextPattern()) / kkmaCount);
//			variables.put("mag.ratio", CardServiceUtil.countSubstring("=MAG>", message.getTextPattern()) / kkmaCount);
//			variables.put("nng.ratio", CardServiceUtil.countSubstring("=NNG>", message.getTextPattern()) / kkmaCount);

//			variables.put("", );
//			variables.put("", );
//			variables.put("", );
//			variables.put("", );			
		}		
//		return variables;	
	}
}



















