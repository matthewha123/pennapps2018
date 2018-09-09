import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

public class JsonArrayParser {

	private JsonArrayParser() {}
	
	public static ListMap parseBody(String body) {
		if (body == null) {
			throw new IllegalArgumentException("parseBody: body is null");
		}
		
		HashMap<Integer, String> map = new HashMap<>();
		ArrayList<Integer> distList = new ArrayList<>();
		
		BufferedReader r = new BufferedReader(new StringReader(body));
		ArrayList<String> lines = new ArrayList<>();
		
		String l;
		try {
			while ((l = r.readLine()) != null) {
				if (!l.contains("[") && !l.contains("]")) {
					lines.add(l);
				} 
			}
		} catch (IOException e) {
			System.out.println("parseBody: IOException caught");
			System.exit(0);
		}
		
		String path, dist;
		int d;
		for (int i = 0; i < lines.size(); i += 2) {
			path = lines.get(i);
			path = path.trim();
			path = path.replace("\"", "");
			path = path.replace(",", "");
			dist = lines.get(i+1);
			dist = dist.trim();
			d = Integer.valueOf(dist);
			map.put(d, path);
			distList.add(d);
		}
		
		return new ListMap(distList, map);
	}
	
}

class ListMap {
	public final HashMap<Integer, String> map;
	public final ArrayList<Integer> list;
	public ListMap(ArrayList<Integer> list, HashMap<Integer, String> map) {
		this.map = map;
		this.list = list;
	}
}
