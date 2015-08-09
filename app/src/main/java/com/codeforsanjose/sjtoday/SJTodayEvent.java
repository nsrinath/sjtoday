package com.codeforsanjose.sjtoday;

import java.net.URL;
import java.util.Date;

public class SJTodayEvent {

	private String title;
	private Date eventStartDateTime;
	private Date eventEndDateTime;
	private URL httpURL;
	private Date publishedDate;
	private String eventAddress;
	private String eventAddressMap;
	private String eventDescription;
	private URL eventCalendarLink;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getEventStartDateTime() {
		return eventStartDateTime;
	}
	public void setEventStartDateTime(Date eventStartDateTime) {
		this.eventStartDateTime = eventStartDateTime;
	}
	public Date getEventEndDateTime() {
		return eventEndDateTime;
	}
	public void setEventEndDateTime(Date eventEndDateTime) {
		this.eventEndDateTime = eventEndDateTime;
	}
	public URL getHttpURL() {
		return httpURL;
	}
	public void setHttpURL(URL httpURL) {
		this.httpURL = httpURL;
	}
	public Date getPublishedDate() {
		return publishedDate;
	}
	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}
	public String getEventAddress() {
		return eventAddress;
	}
	public void setEventAddress(String eventAddress) {
		this.eventAddress = eventAddress;
	}
	public String getEventAddressMap() {
		return eventAddressMap;
	}
	public void setEventAddressMap(String eventAddressMap) {
		this.eventAddressMap = eventAddressMap;
	}
	public String getEventDescription() {
		return eventDescription;
	}
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}
	public URL getEventCalendarLink() {
		return eventCalendarLink;
	}
	public void setEventCalendarLink(URL eventCalendarLink) {
		this.eventCalendarLink = eventCalendarLink;
	}
	@Override
	public String toString() {
		return "SJTodayEvent [title=" + title + ", eventStartDateTime=" + eventStartDateTime + ", eventEndDateTime="
				+ eventEndDateTime + ", httpURL=" + httpURL + ", publishedDate=" + publishedDate + ", eventAddress="
				+ eventAddress + ", eventAddressMap=" + eventAddressMap + ", eventDescription=" + eventDescription
				+ ", eventCalendarLink=" + eventCalendarLink + "]";
	}
	
}
