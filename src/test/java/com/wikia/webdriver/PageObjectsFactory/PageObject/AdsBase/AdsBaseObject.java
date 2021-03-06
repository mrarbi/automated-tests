package com.wikia.webdriver.PageObjectsFactory.PageObject.AdsBase;

import com.wikia.webdriver.Common.ContentPatterns.AdsContent;
import com.wikia.webdriver.Common.ContentPatterns.XSSContent;
import com.wikia.webdriver.Common.Logging.PageObjectLogging;
import com.wikia.webdriver.PageObjectsFactory.PageObject.WikiBasePageObject;
import java.util.Collection;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Bogna 'bognix' Knychala
 */
public class AdsBaseObject extends WikiBasePageObject {

	private Boolean isWikiMainPage;

	private WebElement presentLB;
	private String presentLBName;

	private WebElement presentMD;
	private String presentMDName;

	public AdsBaseObject(WebDriver driver, String page) {
		super(driver);
		AdsContent.setSlotsSelectors();
		getUrl(page);
		isWikiMainPage = checkIfMainPage();
	}

	public void verifyTopLeaderBoardPresent() throws Exception {
		if (isWikiMainPage) {
			presentLB = driver.findElement(
				By.cssSelector(AdsContent.getSlotSelector(AdsContent.homeTopLB))
			);
			presentLBName = AdsContent.homeTopLB;
		} else {
			presentLB = driver.findElement(
				By.cssSelector(AdsContent.getSlotSelector(AdsContent.topLB))
			);
			presentLBName = AdsContent.topLB;
		}
		waitForElementByElement(presentLB);
		checkScriptPresentInSlotScripts(presentLBName, presentLB);
		checkTagsPresent(presentLB);
	}

	public void verifyMedrecPresent() throws Exception {
		if (isWikiMainPage) {
			presentMD = driver.findElement(
				By.cssSelector(AdsContent.getSlotSelector(AdsContent.homeMedrec))
			);
			presentMDName = AdsContent.homeMedrec;
		} else {
			presentMD = driver.findElement(
				By.cssSelector(AdsContent.getSlotSelector(AdsContent.medrec))
			);
			presentMDName = AdsContent.medrec;
		}
		waitForElementByElement(presentMD);
		checkScriptPresentInSlotScripts(presentMDName, presentMD);
		checkTagsPresent(presentMD);
	}

	public void verifyPrefooters() {
		String prefooterSelector = AdsContent.getSlotSelector("Prefooters");
		WebElement prefooterElement = driver.findElement(By.cssSelector(prefooterSelector));

		//Scroll to AIC container and wait for <div> to be present inside it
		scrollToSelector(prefooterSelector);
		checkTagsPresent(prefooterElement);
	}

	public void verifyTopLeaderBoardAndMedrec() throws Exception {
		verifyTopLeaderBoardPresent();
		verifyMedrecPresent();
	}

	public void verifyHubTopLeaderboard() throws Exception {
		String hubLBName = AdsContent.hubLB;
		WebElement hubLB = driver.findElement(By.cssSelector(AdsContent.getSlotSelector(hubLBName)));
		checkScriptPresentInSlotScripts(hubLBName, hubLB);
		PageObjectLogging.log("HUB_TOP_LEADERBOARD found", "HUB_TOP_LEADERBOARD found", true);

		WebElement hubGPT_LB = hubLB.findElement(By.cssSelector(AdsContent.getSlotSelector(AdsContent.hubLB_gpt)));
		PageObjectLogging.log("HUB_TOP_LEADERBOARD_gpt found", "HUB_TOP_LEADERBOARD_gpt found", true);

		if(hubGPT_LB.findElements(By.cssSelector("iframe")).size() > 1) {
			PageObjectLogging.log("IFrames found", "2 IFrames found in HUB_TOP_LEADERBOAD_gpt div", true);
		} else {
			PageObjectLogging.log(
				"IFrames not found",
				"2 IFrames expected to be found in HUB_TOP_LEADERBOAD_gpt div, found less",
				false, driver
			);
			throw new Exception("IFrames inside GPT div not found!");
		}
	}

	public void verifyNoAdsOnPage() throws Exception {
		scrollToSelector(AdsContent.getSlotSelector("AdsInContent"));
		scrollToSelector(AdsContent.getSlotSelector("Prefooters"));
		verifyNoAds();
	}

	public void verifyAdsInContent() {
		String aicSelector = AdsContent.getSlotSelector("AdsInContent");
		WebElement aicContainer = driver.findElement(By.cssSelector(aicSelector));

		//Scroll to AIC container and wait for <div> to be present inside it
		scrollToSelector(aicSelector);
		waitForElementByElement(aicContainer.findElement(By.cssSelector("div")));

		checkTagsPresent(aicContainer);
	}

	private void checkTagsPresent(WebElement slotElement) {
		try {
			waitForOneOfTagsPresentInElement(slotElement, "img", "iframe");
			PageObjectLogging.log(
				"IFrameOrImageFound",
				"Image or iframe was found in slot in less then 30 seconds",
				true,
				driver
			);
		} catch (TimeoutException e) {
			PageObjectLogging.log(
				"IFrameOrImgNotFound",
				"Nor image or iframe was found in slot for 30 seconds",
				false,
				driver
			);
		}
	}

	private List checkScriptPresentInSlotScripts(String slotName, WebElement slotElement) throws Exception {
		List<WebElement> scriptsTags = slotElement.findElements(By.tagName("script"));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		Boolean scriptFound = false;
		String scriptExpectedResult = AdsContent.adsPushSlotScript.replace(
			"%slot%", slotName
		);
		for (WebElement scriptNode : scriptsTags) {
			String result = (String) js.executeScript(
				"return arguments[0].innerHTML", scriptNode
			);
			String trimedResult = result.replaceAll("\\s", "");
			if (scriptExpectedResult.equals(trimedResult)) {
				PageObjectLogging.log(
					"PushSlotsScriptFound",
					"Script " + scriptExpectedResult + " found",
					true
				);
				scriptFound = true;
			}
		}
		if (!scriptFound) {
			PageObjectLogging.log(
				"PushSlotsScriptNotFound",
				"Script " + scriptExpectedResult + " not found",
				false,
				driver
			);
			throw new Exception("Script for pushing ads not found in element");
		}
		return scriptsTags;
	}

	private String createSelectorAll () {
		Collection slotsSelectors = AdsContent.slotsSelectors.values();
		Integer size = slotsSelectors.size();
		Integer i = 1;
		String selectorAll = "";
		for (Object selector : slotsSelectors) {
			selectorAll += (String) selector;
			if (!i.equals(size)) {
				selectorAll += ",";
			}
			i += 1;
		}
		return selectorAll;
	}

	private void scrollToSelector(String selector) {
		if (driver.findElements(By.cssSelector((selector))).size() > 0) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			try {
				js.executeScript(
					"var x = $(arguments[0]);"
					+ "window.scroll(0,x.position()['top']+x.height()+100);"
					+ "$(window).trigger('scroll');",
					selector
				);
			} catch (WebDriverException e) {
				if (e.getMessage().contains(XSSContent.noJQueryError)) {
					PageObjectLogging.log(
						"JSError", "JQuery is not defined", true
					);
				}
			}
		} else {
			PageObjectLogging.log(
				"SelectorNotFound",
				"Selector " + selector + " not found on page",
				false
			);
		}
	}


    private void verifyNoAds() throws Exception {
		List <WebElement> adsElements = driver.findElements(
			By.cssSelector(createSelectorAll())
		);
		if (adsElements.isEmpty()) {
			PageObjectLogging.log(
				"AdsNotFound",
				"Ads not found",
				true,
				driver
			);
		} else {
			for (WebElement element : adsElements) {
				if (element.isDisplayed()
					&& (element.getSize().height > 1 && element.getSize().width > 1)
				) {
					PageObjectLogging.log(
						"AdsFound",
						"Ads found on page",
						false,
						driver
					);
					throw new Exception("Found element that was not expected!");
				}
			}
			PageObjectLogging.log(
				"AdsNotFound",
				"Ads not found",
				true,
				driver
			);
		}
	}
}
