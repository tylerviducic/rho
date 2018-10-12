//  peak_fitter.cpp
//  Created by Torri Roark on 8/30/18.
// code to get histograms, find peaks, fit peaks in histograms, store parameters from fit (sigma, delta sigma, chi2/ndf)

#include <stdio.h>
#include "TCanvas.h"
#include "TVirtualFitter.h"
#include "TMath.h"
#include "TSpectrum.h"
#include "TH1.h"
#include "TF1.h"

Int_t npeaks = 2;
//function to fit peaks
Double_t fpeaks(Double_t *x, Double_t *par) {
    Double_t result = par[0] + par[1]*x[0] + par[2]*x[0]*x[0];
    for (Int_t p=0; p<npeaks; p++) {
        Double_t norm = par[3*p+3];
        Double_t mean = par[3*p+4];
        Double_t sigma = par[3*p+5];
        result += norm*TMath::Gaus(x[0],mean,sigma);
    }
    return result;
}

void peak_fitter(Int_t np =2){
    npeaks = TMath::Abs(np);
    
    Double_t par[5000];
    
    TCanvas *c1 = new TCanvas("c1","display",900,700);
    c1->Divide(5,5,0,0);
    //gStyle->SetOptFit(1111111);
    gStyle->SetOptStat(0);
    
    //file where histograms are located
    //TFile *file = new TFile("crossSectionCorrect_12.root");
    TFile *file = new TFile("/Volumes/MYPASSPORT/PipPimEta.root");
    //get histograms from file, simple change for each file name
    char signal[22];
    for (int k=0; k<=21; k++) {
        stringstream ss;
        ss << k;
        TString str = ss.str();
        sprintf(signal,"%s%d","signal",k);
        TH1F *h = (TH1F*)file->Get("signal"+str);
    
        c1->cd(k+1); // fills the canvas, it starts from 1 instead of 0
        h->Draw();
        
        TH1F *h2 = (TH1F*)h->Clone("h2");
        
        //Use TSpectrum to find peaks in histos
        TSpectrum *s = new TSpectrum(2*npeaks);
        Int_t nfound = s->Search(h,1,""); //(histogram, approx width of signal, conditions )
        printf("Found %d candidate peaks to fit \n",nfound);
        //estimate background using TSpectrum, this and below is from peaks.C code by Rene

        TH1 *hb = s->Background(h,20,"same");
        if (hb) c1->Update();
        if (np < 0) return;

        //fit background with quadratic function
        //c2->cd(k+1);
        TF1 *fline = new TF1("fline","pol2",-0.05,0.05);
        h->Fit("fline");
        par[0] = fline->GetParameter(0);
        par[1] = fline->GetParameter(1);
        par[2] = fline->GetParameter(2);
        npeaks = 0;
        //loop over peaks || figure out yp-sqrt(yp) < fline etc..
        Double_t *xpeaks = s->GetPositionX();
        for (Int_t p=0; p<nfound; p++) {
            Double_t xp = xpeaks[p];
            Int_t bin = h->GetXaxis()->FindBin(xp);
            Double_t yp = h->GetBinContent(bin);
            if (yp-TMath::Sqrt(yp)< fline->Eval(xp)) continue;
            par[3*npeaks+3] = yp;
            par[3*npeaks+4] = xp;
            par[3*npeaks+5] = 0.01; // no clue what this does but if you change it it will probably break
            npeaks++;
        }
        
            printf("Found %d useful peaks to fit \n",npeaks);
            // ("name",fitfunction, min_range, max_range, number of parameters
            TF1 *signal = new TF1("signal",fpeaks,-0.03,0.04,3+3*npeaks);
            TVirtualFitter::Fitter(h2,10+3*npeaks);
            signal->SetParameters(par);
            signal->SetNpx(1000);
            h2->Fit("signal");
        
            //get parameters from fit to calculate N_photons
        
        double chi2 = signal->GetChisquare();
        double ndof = signal->GetNDF();
        
        double N = signal->GetParameter(3);
        double dn = signal->GetParError(3);
        double a = signal->GetParameter(5);
        double da = signal->GetParError(5);
        double pi = 4.0*atan(1.0);
        double b = 0.00125;
        double integral = (sqrt(2*pi)*N*a) /b;
        
        double error = sqrt((dn/N)*(dn/N)+(da/a)*(da/a))*integral;
        cout << " / / / / / / / / / / / / / / / / / / / / / / / / " << endl;
        cout << " " << "integral = " << integral << "+/-" << error << endl;
        cout << " " << "chi2/ndf = " << chi2 / ndof << endl;
        cout << " / / / / / / / / / / / / / / / / / / / / / / / / " << endl;
        
    }
}

