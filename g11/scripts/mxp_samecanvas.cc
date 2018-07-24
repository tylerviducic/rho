void mxp_samecanvas()

{

    //this generates the canvas that will draw the histograms. You can adjust the spacing between the histograms (if you want it) by editing the last two numbers in the line c1->Divide.

    

    //you may need to make more canvas if you have a ton of histos

    

    TCanvas *c1 = new TCanvas("c1","display",900,700);
    TCanvas *c2 = new TCanvas("c2","display",900,700);
    TCanvas *c3 = new TCanvas("c3","display",900,700);

    gStyle->SetOptStat(0);

    c1->Divide(4,5,0,0);
	c2->Divide(4,5,0,0);
	c3->Divide(4,5,0,0);
    

    //This line opens the file where your histograms are saved

    TFile *g = TFile::Open("/home/physics/research/rho/g11/PipPimRHo.root");



    //this loop gets all the histograms from the root file, draws them accordingly

    // you'll need to change the size of the arrays to the number of histos you actually have

    char signal[61];

    char sideband[61];

    char mxp[61];

    for (int k=0; k<=61; k++) //also change your for-loop to the number of histos you have

    {

        stringstream ss;

        ss << k;

        TString str = ss.str();

        

       
        sprintf(signal,"%s%d","signal",k);

        //sprintf(sideband,"%s%d","sideband",k);

        

        TH1F *signal = (TH1F*)g->Get("signal"+str);

     

        if(k<21){



        c1->cd(k+1); 

        //signal->SetFillColor(4);

        signal->Draw();


         

        

        c1->Update();
		}else if(k >=21 && k < 41){



    	    c2->cd(k-20);


    	    signal->Draw();
		} else{
			c3->cd(k-40);


    	    signal->Draw();
		}
    }

}
