package com.yeyanxiang.util.weather;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @update 2014年3月13日
 * 
 * @简介
 */
public class Weather {
	private String update;
	private String weatherlive;
	private String airquality;
	private String travelhelp;
	private DayWeather weather_of_today;
	private DayWeather weather_of_tomorrow;
	private DayWeather weather_of_aftertomorrow;
	private DayWeather weather_of_threedaysfromnow;
	private DayWeather weather_of_fourdaysfromnow;

	public Weather() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public String getWeatherlive() {
		return weatherlive;
	}

	public void setWeatherlive(String weatherlive) {
		this.weatherlive = weatherlive;
	}

	public String getAirquality() {
		return airquality;
	}

	public void setAirquality(String airquality) {
		this.airquality = airquality;
	}

	public String getTravelhelp() {
		return travelhelp;
	}

	public void setTravelhelp(String travelhelp) {
		this.travelhelp = travelhelp;
	}

	public DayWeather getWeather_of_today() {
		return weather_of_today;
	}

	public void setWeather_of_today(DayWeather weather_of_today) {
		this.weather_of_today = weather_of_today;
	}

	public DayWeather getWeather_of_tomorrow() {
		return weather_of_tomorrow;
	}

	public void setWeather_of_tomorrow(DayWeather weather_of_tomorrow) {
		this.weather_of_tomorrow = weather_of_tomorrow;
	}

	public DayWeather getWeather_of_aftertomorrow() {
		return weather_of_aftertomorrow;
	}

	public void setWeather_of_aftertomorrow(DayWeather weather_of_aftertomorrow) {
		this.weather_of_aftertomorrow = weather_of_aftertomorrow;
	}

	public DayWeather getWeather_of_threedaysfromnow() {
		return weather_of_threedaysfromnow;
	}

	public void setWeather_of_threedaysfromnow(
			DayWeather weather_of_threedaysfromnow) {
		this.weather_of_threedaysfromnow = weather_of_threedaysfromnow;
	}

	public DayWeather getWeather_of_fourdaysfromnow() {
		return weather_of_fourdaysfromnow;
	}

	public void setWeather_of_fourdaysfromnow(
			DayWeather weather_of_fourdaysfromnow) {
		this.weather_of_fourdaysfromnow = weather_of_fourdaysfromnow;
	}

	@Override
	public String toString() {
		return "Weather [update=" + update + ", weatherlive=" + weatherlive
				+ ", airquality=" + airquality + ", travelhelp=" + travelhelp
				+ ", weather_of_today=" + weather_of_today
				+ ", weather_of_tomorrow=" + weather_of_tomorrow
				+ ", weather_of_aftertomorrow=" + weather_of_aftertomorrow
				+ ", weather_of_threedaysfromnow="
				+ weather_of_threedaysfromnow + ", weather_of_fourdaysfromnow="
				+ weather_of_fourdaysfromnow + "]";
	}

	public static class DayWeather {
		private String weather;
		private String temperature;
		private String wind;
		private int drawableid1;
		private int drawableid2;

		public DayWeather(String weather, String temperature, String wind,
				int drawableid1, int drawableid2) {
			super();
			this.weather = weather;
			this.temperature = temperature;
			this.wind = wind;
			this.drawableid1 = drawableid1;
			this.drawableid2 = drawableid2;
		}

		public String getWeather() {
			return weather;
		}

		public void setWeather(String weather) {
			this.weather = weather;
		}

		public String getTemperature() {
			return temperature;
		}

		public void setTemperature(String temperature) {
			this.temperature = temperature;
		}

		public String getWind() {
			return wind;
		}

		public void setWind(String wind) {
			this.wind = wind;
		}

		public int getDrawableid1() {
			return drawableid1;
		}

		public void setDrawableid1(int drawableid1) {
			this.drawableid1 = drawableid1;
		}

		public int getDrawableid2() {
			return drawableid2;
		}

		public void setDrawableid2(int drawableid2) {
			this.drawableid2 = drawableid2;
		}

		@Override
		public String toString() {
			return "DayWeather [weather=" + weather + ", temperature="
					+ temperature + ", wind=" + wind + ", drawableid1="
					+ drawableid1 + ", drawableid2=" + drawableid2 + "]";
		}

	}
}
