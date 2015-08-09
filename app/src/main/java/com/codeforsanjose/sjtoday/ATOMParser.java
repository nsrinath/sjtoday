package com.codeforsanjose.sjtoday;


import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndContentImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndLinkImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ATOMParser {

	private static ATOMParser instance;

	private ATOMParser() {

	}

	public static ATOMParser getInstance() {
		if (instance == null)
			return new ATOMParser();
		return instance;
	}

	/**
	 * Returns a list of SJTodayEvents after processing the ATOM feed
	 * @param syndFeed
	 * @return 
	 * @throws IllegalArgumentException
	 * @throws FeedException
	 * @throws IOException
	 * @throws NullPointerException
	 */
	public List<SJTodayEvent> processFeed(SyndFeed syndFeed) throws IllegalArgumentException, FeedException, IOException, NullPointerException {
		List<SJTodayEvent> eventList = new ArrayList<>();
		

		List<SyndEntry> entries = new ArrayList<>();
		for (int i = 0; i < syndFeed.getEntries().size(); i++) {
			entries.add((SyndEntry) syndFeed.getEntries().get(i));
		}

		for (SyndEntry entry : entries) {
			SJTodayEvent sjTodayEvent = new SJTodayEvent();

			sjTodayEvent.setTitle(entry.getTitle());

			// get contents
			List<SyndContentImpl> entryContents = entry.getContents();
			for (SyndContentImpl syndContentImpl: entryContents) {
				String syndContent = syndContentImpl.getValue();
				
				Jsoup.clean(syndContent, Whitelist.basicWithImages());
				Document eventDoc = Jsoup.parseBodyFragment(syndContent);
				
				// get Event Date
				Elements eventStartEndDateTime = eventDoc.select("[datetime]");
				
				try {
					sjTodayEvent.setEventStartDateTime(
							getDateFromString(eventStartEndDateTime.get(0).attr("datetime")));
					sjTodayEvent.setEventEndDateTime(
							getDateFromString(eventStartEndDateTime.get(1).attr("datetime")));
				} catch (IndexOutOfBoundsException e) {
					// that's okay!
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				// get Event Address
				StringBuilder sb = new StringBuilder();
				Elements eventAdd = eventDoc.getElementsByClass("street-address");
				sb.append(eventAdd.text());
				eventAdd = eventDoc.getElementsByClass("locality");
				sb.append(", " + eventAdd.text());
				eventAdd = eventDoc.getElementsByClass("region");
				sb.append(", " + eventAdd.text());
				eventAdd = eventDoc.getElementsByClass("postal-code");
				sb.append(", " + eventAdd.text());
				eventAdd = eventDoc.getElementsByClass("country-name");
				sjTodayEvent.setEventAddress(sb.toString());
				
				// get Event Address Map
				Elements eventMapLoc = eventDoc.select("a[href]");
				for (Element elt : eventMapLoc) {
					if (elt.attr("abs:href").contains("maps.google.com")) {
						sjTodayEvent.setEventAddressMap(elt.attr("abs:href"));
					}
				}
				
				// get Event Description
				Elements eventDesc = eventDoc.getElementsByClass("description");
				sjTodayEvent.setEventDescription(eventDesc.text());
				
			}

			// get Event details URL
			sjTodayEvent.setHttpURL(new URL(entry.getLink()));

			// get event's iCal link
			SyndLinkImpl syndLinkImpl = (SyndLinkImpl) entry.getLinks().get(1);
			if (syndLinkImpl != null)
				sjTodayEvent.setEventCalendarLink(new URL(syndLinkImpl.getHref()));

			// get published date
			sjTodayEvent.setPublishedDate(entry.getPublishedDate());
			
			eventList.add(sjTodayEvent);
			sjTodayEvent = null;
		}

		return eventList;
	}

	private Date getDateFromString(String attr) throws ParseException {
		if (attr.isEmpty())
			return null;
		StringBuilder sb = new StringBuilder();
		sb.append(attr.substring(0, attr.indexOf('T')));
		sb.append(" ");
		sb.append(attr.substring(attr.indexOf('T')+1));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("PST"));
		Date equivalentDate = sdf.parse(sb.toString());
		return equivalentDate;
	}

}
