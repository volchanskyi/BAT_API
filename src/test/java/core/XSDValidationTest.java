package core;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.*;
import java.net.*;
import java.util.*;
import org.testng.*;
import org.testng.annotations.*;
import java.lang.reflect.Method;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;
import okhttp3.*;
import okhttp3.Request.Builder;

public class XSDValidationTest implements ITest {
	String csvFile = "./test_data/csv/bat/xsd_validation.csv";
	// String csvFile = System.getProperty("testcases"); // mvn site
	// -Dtestcases="./input.csv"
	private String test_name = "";

	public String getTestName() {
		return test_name;
	}

	private void setTestName(String a) {
		test_name = a;
	}

	@BeforeMethod(alwaysRun = true)
	public void bm(Method method, Object[] parameters) {
		setTestName(method.getName());
		Override a = method.getAnnotation(Override.class);
		String testCaseId = (String) parameters[a.id()];
		setTestName(testCaseId);
	}

	@DataProvider(name = "dp")
	public Iterator<String[]> a2d() throws InterruptedException, IOException {
		String cvsLine = "";
		String[] a = null;
		ArrayList<String[]> al = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		while ((cvsLine = br.readLine()) != null) {
			a = cvsLine.split(",");
			al.add(a);
		}
		br.close();
		return al.iterator();
	}

	@Override
	@Test(dataProvider = "dp")
	public void test(String tc_id, String xsd_file, String xml_url) throws SAXException, IOException {
		System.out.println(tc_id);
		assertThat(validateXMLSchema(new File(xsd_file), new URL(xml_url)), equalTo(true));
	}

	public static boolean validateXMLSchema(File xsd_file, URL xml_url) throws SAXException, IOException {
		OkHttpClient client = new OkHttpClient();
		Builder b = new Request.Builder();
		ByteArrayInputStream xml_str = new ByteArrayInputStream(
				client.newCall(b.url(xml_url).get().build()).execute().body().string().getBytes());

		SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(xsd_file).newValidator()
				.validate(new StreamSource(xml_str));
		return true;
	}
}
