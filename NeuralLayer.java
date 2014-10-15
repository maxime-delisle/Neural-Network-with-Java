public class NeuralLayer {

	private volatile double [] neurons;
	private boolean oneOrZero;

	public NeuralLayer(int nNeurons, boolean outputInputNeuron) {
		this.neurons = new double [nNeurons];
		this.setOneOrZero(!outputInputNeuron);
	}
	
	public void resetLayer(){
		this.neurons = new double [this.neurons.length];
	}
	
	public void setNeuronValue(int n, double value){
		this.neurons[n] = value;
	}
	public void setNeuronsValue(double[] value){
		this.neurons = value;
	}
	public double getNeuronValue(int n){

		return this.neurons[n];
	}
	public double[] getNeuronsValue(){

		return this.neurons;
	}
	public boolean isOneOrZero() {
		return this.oneOrZero;
	}
	public void setOneOrZero(boolean oneOrZero) {
		this.oneOrZero = oneOrZero;
	}
}
