package com.nitorcreations.nflow.performance;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import com.nitorcreations.nflow.jetty.StartNflow;
import com.nitorcreations.nflow.metrics.NflowMetricsContext;

public class NflowPerfTestServer {
	private final int port;
	private final Map<String, Object> props;
	
	public NflowPerfTestServer(int port, Map<String, Object> props) {
		this.port = port;
		this.props = props;
	}
	
	public void start() throws Exception {
	    new StartNflow().registerSpringContext(NflowMetricsContext.class).startJetty(port, "local", "jmx", props);
	}

	public static void main(String[] args) throws Exception {
		if(args.length < 2) {
			System.out.println("USAGE:");
			System.out.println("NflowPerfTestServer port some/path/node1.properties");
			System.exit(1);
		}
		int port = Integer.parseInt(args[0]);
		String propsFile = args[1];	
		Map<String, Object> props = loadProps(propsFile);
		new NflowPerfTestServer(port, props).start();
	}
	
	private static Map<String, Object> loadProps(String filename) throws IOException {
		try (FileInputStream fis = new FileInputStream(filename)) {
			Properties propsFromFile = new Properties();
			propsFromFile.load(fis);
			Map<String, Object> props = new LinkedHashMap<>();
			for(String key : propsFromFile.stringPropertyNames()) {
				props.put(key, propsFromFile.get(key));
			}
			return props;
		}
	}
}
