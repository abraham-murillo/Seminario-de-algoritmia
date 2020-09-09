#include <iostream>
#include "Lista.h"
using namespace std;

template <class T>
void Lista<T>::insertarInicio(T nvalor){
	Nodo<T>* nuevo = new Nodo<T>(nvalor);
	if( tam == 0 ){
		head = nuevo;
	}else{
		nuevo->sig = head;
		head = nuevo;
	}
	tam++;
}

template <class T>
void Lista<T>::eliminarInicio(){
	if( head ){
		Nodo<T>* phead = head;
		head = head->sig;
		tam--;
		delete phead;
		// ya c:
	}
}

template <class T>
void Lista<T>::mostrar(){
	if( tam == 0 ){
		cout << "Esta vacia\n";
	}else{
		Nodo<T>* yo = head;
		for(int i = 0; i < tam; i++){
			cout << yo->valor << endl; // Me faltó imprimir el valor xd
			yo = yo->sig;
		}
	}
}
