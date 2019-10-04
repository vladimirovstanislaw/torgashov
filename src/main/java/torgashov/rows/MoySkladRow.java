package torgashov.rows;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoySkladRow {
	private String id;
	private String price;
	private String leftOver;

	private static final Pattern pattern = Pattern.compile("(\\,[0-9]*( )*)|(\\.[0-9]*( )*)$");
	private static Matcher matcher = null;
	private static final String replaceWithEmptyString = "";

	public MoySkladRow() {
		super();
	}

	public MoySkladRow(String id, String price, String leftOver) {
		super();
		this.id = id;
		this.price = price;
		this.leftOver = leftOver;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		matcher = pattern.matcher(price);
		this.price = matcher.replaceAll(replaceWithEmptyString);
	}

	public String getLeftOver() {
		return leftOver;
	}

	public void setLeftOver(String leftOver) {
		matcher = pattern.matcher(leftOver);
		this.leftOver = matcher.replaceAll(replaceWithEmptyString);
	}

	@Override
	public String toString() {
		return "MoySkladRow [id=" + id + ", price=" + price + ", leftOver=" + leftOver + "]";
	}

}
