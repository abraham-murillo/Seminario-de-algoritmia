#ifndef PAQUETE_H
#define PAQUETE_H

#include <iostream>
using namespace std;

class Paquete{
private:
	long long id;
	string origen;
	string destino;
	double peso;

public:
	Paquete(){}
	Paquete(long long i, string o, string d, double p){
		id = i;
		origen = o;
		destino = d;
		peso = p;
	}
	// setters
	void setId(int nid){
		id = nid;
	}

	void setOrigen(string &o){
		origen = o;
	}

	void setDestino(string &d){
		destino = d;
	}

	void setPeso(double p){
		peso = p;
	}

	// getters
	long long getId() const{
		return id;
	}

	string getOrigen() const{
		return origen;
	}

	string getDestino() const{
		return destino;
	}

	double getPeso() const{
		return peso;
	}

	friend ostream & operator << (ostream &os, Paquete &p){
		os << "id: " << p.id << '\n';
		os << "origen: " << p.origen << '\n';
		os << "destino: " << p.destino << '\n';
		os << "peso: " << p.peso << '\n';
		return os;
	}

	friend istream & operator >> (istream &is, Paquete &p){
		cout << "Ingrese el id: ";
		is >> p.id;
		cout << "Ingrese el origen: ";
		is >> p.origen;
		cout << "Ingrese el destino: ";
		is >> p.destino;
		cout << "Ingrese el peso: ";
		is >> p.peso;
		return is;
	}
};

#endif
