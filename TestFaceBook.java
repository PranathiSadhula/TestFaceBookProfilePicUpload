package testcases;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.analysis.function.Exp;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestFaceBook {

	// @Test
	public void TC001_testFaceBookSignUp() {
		System.setProperty("webdriver.gecko.driver", "./drivers/geckodriver.exe");
		RemoteWebDriver driver = new FirefoxDriver();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
		JavascriptExecutor js = driver;
		Actions actBuilder = new Actions(driver);

		driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);

		driver.get("https://www.facebook.com");

		System.out.println(driver.getTitle());

		driver.findElement(By.name("firstname")).sendKeys("Shivakshi");
		driver.findElement(By.name("lastname")).sendKeys("Sathi");
		driver.findElement(By.name("reg_email__")).sendKeys("9047769735");
		driver.findElement(By.id("password_step_input")).sendKeys("Pranathi75#");

		Select dayofBirth = new Select(driver.findElement(By.id("day")));
		dayofBirth.selectByValue("25");

		Select mnthofBirth = new Select(driver.findElement(By.id("month")));
		mnthofBirth.selectByVisibleText("Jan");

		Select yearofBirth = new Select(driver.findElement(By.id("year")));
		yearofBirth.selectByIndex(30);

		driver.findElementByXPath("//*[text()='Female']/preceding-sibling::*[@type='radio']").click();

		driver.findElementByName("websubmit").click();

	}

	public static String[][] readExcel(File file) throws InvalidFormatException, IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheetAt(0);
		int rowCount = sheet.getLastRowNum();
		int cellCount = sheet.getRow(0).getLastCellNum();
		String[][] data = new String[rowCount][cellCount];
		for(int rowNum = 1; rowNum <= rowCount; rowNum++ ) {
			for(int cellNum = 0; cellNum < cellCount; cellNum++) {
				String cellvalue = sheet.getRow(rowNum).getCell(cellNum).getStringCellValue();
				data[rowNum-1][cellNum] = cellvalue;
			}
		}
		workbook.close();
		
		return data;
	}

	@DataProvider(name = "LoginDetails")
	public String[][] fetchLoginDetails() throws InvalidFormatException, IOException {
		File file = new File("./data/FaceBook.xlsx");

		return readExcel(file);
	}
	// 77290 27733

	
	@Test(dataProvider = "LoginDetails")
	public void TC002_testFaceBookProfilePicUpload(String loginid,String password,String userName) throws InterruptedException {
		
		System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-notifications");
		RemoteWebDriver driver = new ChromeDriver(options);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
		Actions actBuilder = new Actions(driver);
		driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
		driver.get("https://www.facebook.com/");
		
		driver.findElementById("email").sendKeys(loginid);
		driver.findElementById("pass").sendKeys(password);


		//driver.findElementByName("login").click();
		
		driver.findElementById("loginbutton").click();
		
		wait.until(ExpectedConditions.titleContains("Facebook"));
		
		//*[@title='Profile']
		driver.findElement(By.xpath("//*[@title='Profile']")).click();
		
		wait.until(ExpectedConditions.titleContains(userName));
		
		/*Below code works for profile picture for first time 
		 * wait.until(ExpectedConditions.elementToBeClickable(By.
		 * xpath("//*[contains(@class,'photoContainer')]//*[contains(@alt,'your profile photo')]"
		 * ))); driver.findElement(By.
		 * xpath("//*[contains(@class,'photoContainer')]//*[contains(@alt,'your profile photo')]"
		 * )).click();
		 */
		
		//*[contains(@class,'fbTimelineProfilePicSelector')]//*[contains(@ajaxify,'profile')]
		WebElement profilePic = driver.findElement(By.xpath("//*[contains(@class,'fbTimelineProfilePicSelector')]//*[contains(@ajaxify,'profile')]"));
		actBuilder.moveToElement(profilePic).click().build().perform();
		
		File profilePicFile = new File("./data/"+userName+".jpg") ;
		String profilePicpath = profilePicFile.getAbsolutePath();
		
		driver.findElement(By.xpath("//*[@data-action-type='upload_photo']//*[@title='Choose a file to upload']")).sendKeys(profilePicpath);
		//driver.close();
		
		driver.findElement(By.xpath("//*[@role='dialog']//button[text()='Save']")).click();
	}
}
