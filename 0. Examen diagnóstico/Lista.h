#ifndef LISTA_H
#define LISTA_H

#include <iostream>
using namespace std;

template <class T>
class Nodo{
public: // Mala práctica, perdón
	T valor;
	Nodo<T>* sig;

	Nodo(){
		sig = NULL;
	}

	Nodo(T v){
		valor = v;
		sig = NULL;
	}
};

template <class T>
class Lista{
private:
	Nodo<T>* head;
	int tam;
public:
	Lista(){
		head = NULL;
		tam = 0;
	}
	void insertarInicio(T nvalor);
	void eliminarInicio();
	void mostrar();
};

#endif
