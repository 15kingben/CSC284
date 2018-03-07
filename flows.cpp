#include <iostream>
#include <vector>
#include <algorithm>
#include <queue>

using namespace std;

const int MAX_VERTEX = 1001;


bool bfs(int resid[MAX_VERTEX][MAX_VERTEX], int source, int sink, int bp[MAX_VERTEX]){
  queue<int> neighbors;
  bool visited[MAX_VERTEX];
  for (int i = 0; i < MAX_VERTEX; i++){
    visited[i] = false;
  }

  neighbors.push(source);
  bp[source] = -1;
  visited[source] = true;

  while(true){
    if (neighbors.empty()){
      return false;
    }

    int next = neighbors.front();
    neighbors.pop();
    if (next == sink){
      return true;
    }else{
      for(int j = 0; j < MAX_VERTEX; j++){
        // if (!adj[next][j] == 0)
        //   cout << next << " " << j << " " << visited[j] << " " << adj[next][j] << endl;
        if (!visited[j] && resid[next][j] > 0){
          visited[j] = true;
          neighbors.push(j);
          bp[j] = next;
          // cout << j << endl;
        }
      }
    }

  }
}


void solve_problem(){
  int num_vertices, num_edges;
  cin >> num_vertices;
  cin >> num_edges;
  int source, sink;
  cin >> source;
  cin >> sink;
  int adj[MAX_VERTEX][MAX_VERTEX];
  int max_vertex = -1;
  for (int i = 0; i < num_edges; i++){
    int u,v,c;
    cin >> u;
    cin >> v;
    cin >> c;
    adj[u][v] = c;
    max_vertex = max(max_vertex, max(u,v));
  }

  int resid[MAX_VERTEX][MAX_VERTEX];
  for(int i = 0; i < max_vertex + 1; i++){
    for(int j = 0; j < max_vertex + 1; j++){
      resid[i][j] = adj[i][j];
    }
  }


  int max_flow = 0;
  int bp[MAX_VERTEX];
  while(bfs(resid, source, sink, bp)){
    // cout << "poop" << endl;
    int u = bp[sink];
    int v = sink;
    int max_width = 10000000;
    while(u != -1){
      max_width = min(max_width, resid[u][v]);
      v = u;
      u = bp[u];
    }
    u = bp[sink];
    v = sink;
    while(u != -1){
      resid[u][v] -= max_width;
      resid[v][u] += max_width;
      v = u;
      u = bp[u];
    }


    max_flow+=max_width;
    for(int i = 0; i < MAX_VERTEX; i++){
      bp[i] = -1;
    }
  }


  cout << max_flow << endl;


}

int main(int argc, char** argv){
  int num_problems;
  cin >> num_problems;
  while (num_problems > 0){
    solve_problem();
    num_problems--;
    // cout << endl << endl;
  }
  return 1;
}
