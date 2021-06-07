import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

class AlgGrafos {

    public static void main(String[] args) {
        Scanner entradaUsuario = new Scanner(System.in);
        String menu = "\n\n 0 Sair \n 1 Ler de arquivo texto \n 2 Classificar Grafo";
        Graph g3 = new Graph();
        while (true) {
            System.out.println(menu);
            System.out.println("--------------------------------------------------------------------");
            System.out.println("Digite a opção desejada:");
            int opcao = entradaUsuario.nextInt();
            switch (opcao) {
                case 0:
                    return;
                case 1:
                    String arq_ent = "myfiles/grafo01.txt";
                    g3.open_text(arq_ent);
                    break;
                case 2:
                    g3.bicon_comp();
                    break;
            }
        }
    }

}

class Graph {
    Stack<Vertex> st1;
    Set<Vertex> componentesBiconexas;
    HashMap<Integer, Vertex> vertex_set;
    int time;
    Boolean acyclic;
    Boolean isClique = true;

    public Graph() {
        vertex_set = new HashMap<>();
    }

    void bicon_comp() {
        st1 = new Stack<>();
        reset();
        for (Vertex v1 : vertex_set.values()) {
            if (v1.d == null) {
                bicon_comp_visit(v1);
            }
        }
        if (isClique) {
            System.out.println("\nEste é um grafo bloco pois todas as suas Componentes Biconexas Maximal são cliques \n");
        } else {
            System.out.println("\nEste NÃO é um grafo bloco pois existem Componentes Biconexas Maximal que não são cliques \n");
        }
    }

    void bicon_comp_visit(Vertex v1) {
        v1.d = ++time;
        v1.low = v1.d;
        for (Vertex neig : v1.nbhood.values()) {
            if (neig.d == null) {
                st1.push(v1); // aresta de árvore
                st1.push(neig);
                neig.parent = v1;
                bicon_comp_visit(neig);
                if (neig.low >= v1.d) // comp. biconexa detectada
                    this.print_bic_comp(v1, neig);
                if (neig.low < v1.low)
                    v1.low = neig.low;
            } else if (neig != v1.parent) {
                if (neig.d < v1.d) {  // aresta de retorno
                    st1.push(v1);
                    st1.push(neig);
                    if (neig.d < v1.low)
                        v1.low = neig.d;
                }
                // else aresta já explorada
            }
        }
    }

    void print_bic_comp(Vertex cut_vertex, Vertex aux) {
        componentesBiconexas = new HashSet<>();
        if (st1.empty()) {
            return;
        }
        System.out.print("\nComponente Biconexa: ");
        Vertex v1 = this.st1.pop();
        componentesBiconexas.add(v1);
        Vertex v2 = this.st1.pop();
        componentesBiconexas.add(v2);
        System.out.printf("%d,%d  ", v1.id, v2.id);
        while (v1 != cut_vertex || v2 != aux) {
            if (st1.empty()) {
                classificarGrafo();
                return;
            }
            v1 = this.st1.pop();
            componentesBiconexas.add(v1);
            v2 = this.st1.pop();
            componentesBiconexas.add(v2);
            System.out.printf("%d,%d  ", v1.id, v2.id);
        }
    }

    void classificarGrafo() {
        for (Vertex vertice : componentesBiconexas) {
            if (!(vertice.degree() == componentesBiconexas.size() - 1)) {
                isClique = false;
                System.out.println("Não é uma clique");
                return;
            }
        }
        System.out.println("É uma clique");
    }

    void add_edge(Integer id1, Integer id2) {
        Vertex v1 = vertex_set.get(id1);
        Vertex v2 = vertex_set.get(id2);
        if (v1 == null || v2 == null) {
            System.out.print("Vértice inexistente!");
            return;
        }
        v1.add_neighbor(v2);
        v2.add_neighbor(v1);
        reset();
    }

    void add_vertex(int id) {
        if (id < 1 || this.vertex_set.get(id) == null) {
            Vertex v = new Vertex(id);
            vertex_set.put(v.id, v);
            reset();
        } else
            System.out.println("Id inválido ou já utilizado!");
    }

    void reset() {
        acyclic = null;
        time = 0;
        for (Vertex v1 : vertex_set.values())
            v1.reset();
    }

    void open_text(String arq_ent) {
        String thisLine = null;
        vertex_set = new HashMap<Integer, Vertex>();
        String[] pieces;

        try {
            FileReader file_in = new FileReader(arq_ent);
            BufferedReader br1 = new BufferedReader(file_in);
            while ((thisLine = br1.readLine()) != null) {
                // retira excessos de espaços em branco
                thisLine = thisLine.replaceAll("\\s+", " ");
                pieces = thisLine.split(" ");
                int v1 = Integer.parseInt(pieces[0]);
                this.add_vertex(v1);
                for (int i = 2; i < pieces.length; i++) {
                    int v2 = Integer.parseInt(pieces[i]);
                    // pode ser a primeira ocorrência do v2
                    this.add_vertex(v2);
                    this.add_edge(v1, v2);
                }
            }
            System.out.print("Arquivo lido com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class Vertex {
    Integer id;
    HashMap<Integer, Vertex> nbhood;
    Vertex parent;
    Integer dist, d, f;
    Integer low;

    Vertex(int id) {
        this.id = id;
        nbhood = new HashMap<>();
        parent = null;
        dist = d = null;
    }

    void reset() {
        parent = null;
        d = null;
        f = null;
        dist = null;
    }

    void add_neighbor(Vertex viz) {
        nbhood.put(viz.id, viz);
    }

    int degree() {
        return nbhood.size();
    }
}
