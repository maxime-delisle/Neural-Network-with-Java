import java.util.ArrayList;


public class NeuralNet {

	private double weights[];

	private volatile ArrayList <NeuralLayer> listLayers = new ArrayList<NeuralLayer>();

	public NeuralNet(int inputs, int firstLayer, int outputs){

		listLayers.add(new NeuralLayer(inputs, true));
		listLayers.add(new NeuralLayer(firstLayer, false));
		listLayers.add(new NeuralLayer(outputs,true));

	}

	public NeuralNet(int inputs, int firstLayer, int secondLayer, int outputs){

		listLayers.add(new NeuralLayer(inputs, true));
		listLayers.add(new NeuralLayer(firstLayer,false));
		listLayers.add(new NeuralLayer(secondLayer,false));
		listLayers.add(new NeuralLayer(outputs,true));

	}

	public void setWeights(double [] gene){
		this.weights = gene;
	}

	public void resetLayers(){
		for (NeuralLayer nl: listLayers)
			nl.resetLayer();
	}

	public double[] getOutputs(double[] inputs){

		this.resetLayers();
		int gene = 0;
		int layer = 0;
		for (NeuralLayer nl : listLayers){
			if (nl == listLayers.get(0)){
				nl.setNeuronsValue(inputs);
			}
			else {
				int l = 0;
				for (double n : nl.getNeuronsValue()){
					for (double nPrec : listLayers.get(layer-1).getNeuronsValue()){
						n = n + (nPrec*this.weights[gene]);
						nl.setNeuronValue(l,n);
						gene++;
					}
					if (n >= this.weights[gene] & (nl.isOneOrZero())){
						nl.setNeuronValue(l,1);
					}
					else if (nl.isOneOrZero())
						nl.setNeuronValue(l,0);
					gene++;	
					l++;
				}
			}
			if (nl == listLayers.get(listLayers.size()-1)){
				return nl.getNeuronsValue();
			}
			layer++;
		}
		System.out.println("Error");
		return null;

	}

}
