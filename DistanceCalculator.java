import javax.script.*;


public class DistanceCalculator {
	private String distanceFunction;
	private boolean weightingByDistance;
	
	public static void main(String[] args) {
		// TESTS
		double[] v1 = {1,2,3,5,6,7,8};
		double[] v2 = {2,5,4,5,6,7,9};
		double[][] vectors = new double[2][];
		vectors[0] = v1;
		vectors[1] = v2;
		
		String f = "function(v1,v2) {" +
				"return v1[0]/2 + v2[3] + 8 + Math.pow(5,2)" +
				"}";

		DistanceCalculator d = new DistanceCalculator("euclidean",true);
		
		d.setDistanceFunction(f);
		double distance = d.getDistance(vectors);
		System.out.println(distance);
		System.out.println(d.weight(distance));
		
		
	}
	
	public DistanceCalculator(String distanceFunction, boolean weightingByDistance) {
		this.distanceFunction = distanceFunction;
		this.weightingByDistance = weightingByDistance;
	}
	
	
	public double getDistance(final double[][] vectors) {

		switch(this.distanceFunction) {
			case "euclidean":
				return this.getMinkowskiDistance(vectors, 2);
			case "manhattan":
				return this.getMinkowskiDistance(vectors, 1);
			default:
				return this.getCustomDistance(vectors);
		}
		
	}
	
	private double getCustomDistance(final double[][] vectors) {
		final double[] v1 = vectors[0];
		final double[] v2 = vectors[1];
		int dimensions = v1.length > v2.length ? v2.length : v1.length;
		double distance = 0;
		
		
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		try {
			engine.eval("var v1 = []");
			engine.eval("var v2 = []");
			for(int i = 0; i < dimensions; i++) {
				engine.eval(String.format("v1.push(%s)",v1[i]));
				engine.eval(String.format("v2.push(%s)",v2[i]));
			}
			engine.eval(String.format("var f = %s",this.distanceFunction));
			engine.eval("var distance = f(v1,v2)");
			distance = (double)engine.getBindings(ScriptContext.ENGINE_SCOPE).get("distance");
		} catch(ScriptException e) {
			System.out.println("Error in Javascript");
			return Double.POSITIVE_INFINITY;
		}
		return distance;
	}
	
	private double getMinkowskiDistance(final double[][] vectors, int p) {
		final double[] v1 = vectors[0];
		final double[] v2 = vectors[1];
		int dimensions = v1.length > v2.length ? v2.length : v1.length;
		double distance = 0;
		
		for(int i = 0; i < dimensions; i++) {
			distance += Math.pow(Math.abs(v1[i]-v2[i]),p);
		}
		distance = Math.pow(distance,(double)1/p);
		return distance;
	}
	
	public void setDistanceFunction(String distanceFunction) {
		this.distanceFunction = distanceFunction;
	}
	
	public void setWeightingByDistance(boolean value) {
		this.weightingByDistance = value;
	}
	
	
	public double weight(double distance) {
		if(this.weightingByDistance != true) {
			return 1;
		} else {
			return (double)1/Math.pow(distance,2);
		}
	}
}
