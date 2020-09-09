package paquete1;

public class DisjointSet {
    int[] pr;
    int[] tot;

    DisjointSet(int nn){
        pr = new int[nn + 1];
        tot = new int[nn + 1];
        for (int u = 0; u <= nn; u++) {
            pr[u] = u;
            tot[u] = 1;
        }
    }

    int find(int u) {
        if( pr[u] == u ){
            return u;
        }
        return pr[u] = find(pr[u]);
    }

    boolean unite(int u, int v){
        u = find(u);
        v = find(v);
        if( u != v ){
            pr[u] = v;
            tot[v] += tot[u];
            return true;
        }
        return false;
    }

    boolean same(int u, int v){
        return find(u) == find(v);
    }
}
