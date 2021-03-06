package com.wikia.webdriver.PageObjectsFactory.PageObject.Mobile;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.PageFactory;

import com.wikia.webdriver.Common.Core.Assertion;
import com.wikia.webdriver.Common.Core.Global;
import com.wikia.webdriver.Common.Logging.PageObjectLogging;

public class MobileArticlePageObject extends MobileBasePageObject{

	public MobileArticlePageObject(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css="#wkArtCom .collSec.addChev")
	private WebElement commentsSectionShowButton;
	@FindBy(css=".commSbt.wkBtn.main")
	private WebElement postCommentButton;
	@FindBy(css=".commText[name='wpArticleComment']")
	private WebElement commentInputArea;
	@FindBy(css=".cmnRpl")
	private WebElement replyCommentButton;
	@FindBy(css="[placeholder='Post a reply']")
	private WebElement replyInputArea;
	@FindBy(css="#wkMdlCnt [name='wpArticleSubmit']")
	private WebElement replySubmitButton;
	@FindBy(css="#wkMdlClo")
	private WebElement closeModalButton;
	@FindBy(css=".collSec.addChev.open")
	private WebElement commentsSectionHideButton;
	@FindBy(css="#commMore")
	private WebElement loadMoreCommentsButton;
	@FindBy(css="#commPrev")
	private WebElement loadPreviousCommentsButton;
	@FindBy(css="#toctitle")
	private WebElement tocWrapper;
	@FindBy(css=".artSec.open .goBck")
	private WebElement hideSectionButton;
	@FindBy(xpath="//div/figure[@class='thumb']")
	private WebElement modalWrapper;
	@FindBy(xpath="//div/figure[@class='thumb']/img")
	private WebElement imageModalTrigger;
	@FindBy(xpath="//section[@class='swiperPage current']")
	private WebElement currentImageModal;
	@FindBy(css=".toc:not(.open)")
	private WebElement tocClosed;
	@FindBy(css=".toc.open")
	private WebElement tocOpened;
	@FindBy(css=".toctext")
	private WebElement tocText;
	@FindBys(@FindBy(css=".toclevel-1>a"))
	private List<WebElement> tocLevel1Sections;
	@FindBys(@FindBy(css=".toclevel-2>a"))
	private List<WebElement> tocLevel2Sections;
	@FindBys(@FindBy(css=".toclevel-3>a"))
	private List<WebElement> tocLevel3Sections;
	@FindBys(@FindBy(css=".toclevel-4>a"))
	private List<WebElement> tocLevel4Sections;
	@FindBy(css="section.artSec.open")
	private WebElement sectionOpened;


	//@FindBy(css="h2.collSec")
	//private WebElement sectionClosed;

	private void showCommentsSection(){
		waitForElementByElement(commentsSectionShowButton);
		clickAndWait(commentsSectionShowButton);
		waitForElementByElement(postCommentButton);
		waitForElementByElement(commentInputArea);
		PageObjectLogging.log("showCommentsSection", "comments sections is visible", true, driver);
	}

	private void verifyAddedComment(String comment){
		waitForElementByXPath("//li[@class='comment']/blockquote/div[@class='txt']/p[contains(text(), '"+comment+"')]");
		PageObjectLogging.log("verifyAddedComment", "comment "+comment+" is visible", true, driver);
	}

	public void addComment(String comment){
		showCommentsSection();
		commentInputArea.sendKeys(comment);
		commentInputArea.sendKeys(Keys.TAB);
		clickAndWait(postCommentButton);
		verifyAddedComment(comment);
		PageObjectLogging.log("addComment", "comment "+comment+" added", true);
	}

	private void verifyAddedReplyOnCommentPage(String reply){
		waitForElementByXPath("//div[@id='wkMdlWrp']//ul[@class='sub-comments']//p[contains(text(), '"+reply+"')]");
		PageObjectLogging.log("verifyAddedReply", "reply "+reply+" is visible", true, driver);
	}

	public void addReply(String reply){
		waitForElementByElement(replyCommentButton);
		String url = driver.getCurrentUrl();
		executeScript("document.querySelectorAll('.cmnRpl')[0].click()");
//		clickAndWait(replyCommentButton);
		verifyURL(url+"#Modal");
		replyInputArea.sendKeys(reply);
		replyInputArea.submit();
		verifyURL(url+"#Modal");
		verifyAddedReplyOnCommentPage(reply);
		PageObjectLogging.log("addReply", "reply "+reply+" added", true, driver);
	}

	public void verifyPagination()
	{
		showCommentsSection();
		waitForElementByElement(loadMoreCommentsButton);
		clickAndWait(loadMoreCommentsButton);
		//loadMoreCommentsButton.click();
		waitForElementByElement(loadPreviousCommentsButton);
		loadPreviousCommentsButton.click();
		waitForElementByElement(loadMoreCommentsButton);
		PageObjectLogging.log("verifyPagination", "pagination added", true);
	}

	public MobileArticlePageObject openSections(){
		getUrl(Global.DOMAIN+"wiki/Sections");
		waitForElementByElement(tocWrapper);
		PageObjectLogging.log("openSections", "sections page was opened", true, driver);
		return new MobileArticlePageObject(driver);
	}

	public MobileArticlePageObject openTOCPage(){
		getUrl(Global.DOMAIN+"wiki/TOC");
		waitForElementByElement(tocWrapper);
		PageObjectLogging.log("openTOC", "TOC page was opened", true, driver);
		return new MobileArticlePageObject(driver);
	}

	public void clickSection(int sectionNumber){
		WebElement chev = waitForElementByXPath("//div[@class='mw-content-ltr']/h2["+sectionNumber+"]");
		chev.click();
		PageObjectLogging.log("clickSection", "section "+chev.getText()+" clicked", true, driver);

	}

	public void verifySectionVisibility(){
		waitForElementByCss("div.mw-content-ltr h2.collSec.open");
		PageObjectLogging.log("verifySectionVisibility", "section is opened and visible", true, driver);
	}

	public void verifySectionInvisibility(){
		waitForElementByCss("div.mw-content-ltr h2.collSec:not(.open)");
		PageObjectLogging.log("verifySectionInvisibility", "section is not visible", true, driver);
	}

	public void clickHideButton(){
		waitForElementByElement(hideSectionButton);
		executeScript("document.querySelectorAll('.artSec.open .goBck')[0].click()");
		PageObjectLogging.log("clickHideButton", "hide section button clicked", true, driver);
	}

	public MobileArticlePageObject openModals(){
		getUrl(Global.DOMAIN+"wiki/Modal");
		waitForElementByElement(modalWrapper);
		PageObjectLogging.log("openModals", "modals page was opened", true, driver);
		return new MobileArticlePageObject(driver);
	}


	public MobileModalComponentObject clickModal(){
		String url = driver.getCurrentUrl();
		clickAndWait(imageModalTrigger);
		PageObjectLogging.log("clickModal", "modal trigger clicked", true, driver);
		Assertion.assertEquals(url+"#Modal", driver.getCurrentUrl());
		waitForElementByElement(currentImageModal);
		PageObjectLogging.log("clickModal", "modal url verified", true, driver);
		return new MobileModalComponentObject(driver);

	}

	public void verifyTocOpened(){
		waitForElementByElement(tocOpened);
		PageObjectLogging.log("verifyTocOpened", "verified toc opened", true);
	}

	public void verifyTocClosed(){
		waitForElementByElement(tocClosed);
		PageObjectLogging.log("verifyTocClosed", "verified toc closed", true);
	}

	public void clickChevronToOpenToc(){
		waitForElementByElement(tocClosed);
		tocClosed.click();
		PageObjectLogging.log("clickChevronToChangeTocState", "toc state changed", true);
	}

	public void clickChevronToCloseToc(){
		waitForElementByElement(tocOpened);
		tocOpened.click();
		PageObjectLogging.log("clickChevronToChangeTocState", "toc state changed", true);
	}

	public String clickOnLevel1SectionInToc(int number){
		WebElement tocElement = tocLevel1Sections.get(number);
		String href = tocElement.getAttribute("href");
		tocElement.click();
		PageObjectLogging.log("clickOnLevel1SectionInToc", "toc level 1 clicked", true);
		return href.replace(Global.DOMAIN+"wiki/TOC#","");
	}

	public String clickOnLevel2SectionInToc(int number){
		WebElement tocElement = tocLevel2Sections.get(number);
		String href = tocElement.getAttribute("href");
		tocElement.click();
		PageObjectLogging.log("clickOnLevel2SectionInToc", "toc level 2 clicked: "+href, true);
		return href.replace(Global.DOMAIN+"wiki/TOC#","");
	}

	public String clickOnLevel3SectionInToc(int number){
		WebElement tocElement = tocLevel3Sections.get(number);
		String href = tocElement.getAttribute("href");
		tocElement.click();
		PageObjectLogging.log("clickOnLevel3SectionInToc", "toc level 3 clicked: "+href, true);
		return href.replace(Global.DOMAIN+"wiki/TOC#","");
	}

	public String clickOnLevel4SectionInToc(int number){
		WebElement tocElement = tocLevel4Sections.get(number);
		String href = tocElement.getAttribute("href");
		tocElement.click();
		PageObjectLogging.log("clickOnLevel4SectionInToc", "toc level 4 clicked: "+href, true);
		return href.replace(Global.DOMAIN+"wiki/TOC#","");
	}

	public void verifySectionHeaderOpened(String desiredId){
		waitForElementByElement(sectionHeaderOpened);
		String currentId = sectionHeaderOpened.getAttribute("id");
		Assertion.assertEquals(desiredId, currentId);
		PageObjectLogging.log("verifySectionHeaderOpened", "header section opened", true);
	}

	public void verifySectionOpened(String desiredId, int level){
		waitForElementByElement(sectionOpened);
		sectionOpened.findElement(By.cssSelector("h"+level+"#"+desiredId));
		PageObjectLogging.log("verifySectionLevel" + level + "Opened", "section opened", true);
	}


}
