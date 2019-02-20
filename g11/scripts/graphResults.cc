#include <iostream>
#include <fstream>
#include <cmath>


void graphResults(){

	Double_t IM_PipPim[50];
	Double_t Nphotons[50]; 
	Double_t Ne[50];
	Double_t Egam[50];
	Int_t k = 0;
	
	ifstream file;
	file.open("/home/physics/research/rho/g11/scripts/fittedSignalResults.txt");
	
	if(file.fail()){
		cout << "error reading file" << endl;
		return -1;
	}else{
		while(!file.eof()){
			file >> IM_PipPim[k] >> Nphotons[k] >> Ne[k];
			k++;
		}
	
	}
	
	for(int i = 0; i <50; i++){
		Egam[i] = (.770 * .770 - IM_PipPim[i]*IM_PipPim[i])/(2 * .770);
	}
	
	TGraphErrors *g1 = new TGraphErrors(50, Egam, Nphotons,0, Ne);
	g1->SetTitle("N vs. Egam");
	g1->Draw();	

}
