package com.codeforsanjose.sjtoday;


import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndContentImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndLinkImpl;

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

	final private static ATOMParser mInstance = new ATOMParser();

	private ATOMParser() {

	}

	public static ATOMParser getInstance() {
		return mInstance;
	}

	/**
	 * Returns a list of SJTodayEvents after processing the ATOM feed
	 * @param syndFeed gets a SyndFeed as input to process
	 * @return returns a list of SJTodayEvent objects
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws NullPointerException
	 */
	public List<SJTodayEvent> processFeed(SyndFeed syndFeed) throws IllegalArgumentException,
            IOException, NullPointerException {
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
            if (entryContents == null || entryContents.size() == 0) {
                return null;
            }
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
                String s = "" . concat(eventDoc.getElementsByClass("street-address").text())
                        .concat(", ")
                        .concat(eventDoc.getElementsByClass("locality").text())
                        .concat(", ")
                        .concat(eventDoc.getElementsByClass("region").text())
                        .concat(", ")
                        .concat(eventDoc.getElementsByClass("country-name").text())
                        .concat(", ")
                        .concat(eventDoc.getElementsByClass("postal-code").text());

				sjTodayEvent.setEventAddress(s);
				
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
		}

		return eventList;
	}

	private Date getDateFromString(String attr) throws ParseException {
		if (attr.isEmpty())
			return null;

        String sb = ""
                .concat(attr.substring(0, attr.indexOf('T')))
                .concat(" ")
                .concat(attr.substring(attr.indexOf('T')+1));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("PST"));
		return sdf.parse(sb);
	}

}
