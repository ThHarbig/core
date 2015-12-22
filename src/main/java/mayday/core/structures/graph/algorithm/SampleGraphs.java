package mayday.core.structures.graph.algorithm;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;

public class SampleGraphs 
{
	public static Graph A()
	{
		Graph A= new Graph();
		Node Aa=new Node(A,"Aa");
		Node Ab=new Node(A,"Ab");
		Node Ac=new Node(A,"Ac");
		
		A.addNode(Aa);
		A.addNode(Ab);
		A.addNode(Ac);
		
		A.connect(Aa,Ab);
		A.connect(Ab,Ac);
		A.connect(Ac,Aa);
		
		return A;
	}
	
	public static Graph B()
	{
		Graph B= new Graph();
		Node Ba=new Node(B,"Ba");
		Node Bb=new Node(B,"Bb");
		Node Bc=new Node(B,"Bc");
		Node Bd=new Node(B,"Bd");
		
		B.addNode(Ba);
		B.addNode(Bb);
		B.addNode(Bc);
		B.addNode(Bd);
		
		B.connect(Ba,Bb);
		B.connect(Bb,Bc);
		B.connect(Bb,Bd);
		
		return B;
	}
	
	public static Graph C()
	{
		Graph C=new Graph();
		Node Ca=new Node(C,"Ca");
		Node Cb=new Node(C,"Cb");
		Node Cc=new Node(C,"Cc");
		Node Cd=new Node(C,"Cd");
		Node Ce=new Node(C,"Ce");
		Node Cf=new Node(C,"Cf");
		Node Cg=new Node(C,"Cg");
		
		C.addNode(Ca);
		C.addNode(Cb);
		C.addNode(Cc);
		C.addNode(Cd);
		C.addNode(Ce);
		C.addNode(Cf);
		C.addNode(Cg);
		
		C.connect(Ca,Cb);
		C.connect(Ca,Cc);
		C.connect(Ca,Cd);
		
		C.connect(Cb,Ce);
		C.connect(Cb,Cf);
		C.connect(Cc,Cf);
		C.connect(Ce,Cg);
		C.connect(Cf,Cg);
		
		return(C);
	}
	
	public static Graph D()
	{
		Graph D=new Graph();
		Node Da=new Node(D,"Da");
		D.addNode(Da);
		Node Db=new Node(D,"Db");
		D.addNode(Db);
		Node Dc=new Node(D,"Dc");
		D.addNode(Dc);
		Node Dd=new Node(D,"Dd");
		D.addNode(Dd);
		Node De=new Node(D,"De");
		D.addNode(De);
		Node Df=new Node(D,"Df");
		D.addNode(Df);
		
		D.connect(Da,Db);
		D.connect(Db,Dc);
		D.connect(Dc,Dd);
		D.connect(Dd,De);
		D.connect(Dd,Df);
		D.connect(Df,Da);
		
		return D;
	}
	
	public static Graph E()
	{
		Graph E=new Graph();
		Node Ea=new Node(E,"Ea");
		E.addNode(Ea);
		Node Eb=new Node(E,"Eb");
		E.addNode(Eb);
		Node Ec=new Node(E,"Ec");
		E.addNode(Ec);
		Node Ed=new Node(E,"Ed");
		E.addNode(Ed);
		Node Ee=new Node(E,"Ee");
		E.addNode(Ee);
		Node Ef=new Node(E,"Ef");
		E.addNode(Ef);
		Node Eg=new Node(E,"Eg");
		E.addNode(Eg);
		Node Eh=new Node(E,"Eh");
		E.addNode(Eh);
		Node Ei=new Node(E,"Ei");
		E.addNode(Ei);
		
		E.connect(Ea,Eb);
		E.connect(Eb,Ec);
		E.connect(Ec,Ed);
		E.connect(Ed,Ee);
		E.connect(Ee,Ef);
		E.connect(Ef,Ea);
		E.connect(Ed,Eg);
		E.connect(Eg,Eh);
		E.connect(Eg,Ei);
		
		return E;
	}
	
	public static Graph F()
	{
		Graph F=new Graph();
		Node Fa=new Node(F,"Fa");
		F.addNode(Fa);
		Node Fb=new Node(F,"Fb");
		F.addNode(Fb);
		Node Fc=new Node(F,"Fc");
		F.addNode(Fc);
		Node Fd=new Node(F,"Fd");
		F.addNode(Fd);
		Node Fe=new Node(F,"Fe");
		F.addNode(Fe);
		Node Ff=new Node(F,"Ff");
		F.addNode(Ff);
		Node Fg=new Node(F,"Fg");
		F.addNode(Fg);
		Node Fh=new Node(F,"Fh");
		F.addNode(Fh);
		Node Fi=new Node(F,"Fi");
		F.addNode(Fi);
		Node Fj=new Node(F,"Fj");
		F.addNode(Fj);
		
		F.connect(Fa,Fb);		
		F.connect(Fb,Fc);
		
		F.connect(Fc,Fd);
		F.connect(Fd,Fe);
		F.connect(Fe,Ff);
		F.connect(Ff,Fg);
		F.connect(Fg,Fc);
		
		F.connect(Ff,Fh);
		F.connect(Fh,Fi);
		F.connect(Fh,Fj);
		return F;
	}
	
	public static Graph G()
	{
		Graph G=new Graph();
		Node Ga=new Node(G,"Ga");
		G.addNode(Ga);
		Node Gb=new Node(G,"Gb");
		G.addNode(Gb);
		Node Gc=new Node(G,"Gc");
		G.addNode(Gc);
		Node Gd=new Node(G,"Gd");
		G.addNode(Gd);
		Node Ge=new Node(G,"Ge");
		G.addNode(Ge);
		Node Gf=new Node(G,"Gf");
		G.addNode(Gf);
		Node Gg=new Node(G,"Gg");
		G.addNode(Gg);
		Node Gh=new Node(G,"Gh");
		G.addNode(Gh);
		Node Gi=new Node(G,"Gi");
		G.addNode(Gi);
		Node Gj=new Node(G,"Gj");
		G.addNode(Gj);
		
		G.connect(Ga,Gb);	
		
		G.connect(Gb,Gc);	
		
		G.connect(Gc,Gd);
		G.connect(Gd,Ge);
		G.connect(Ge,Gb);
		
		G.connect(Gc,Gf);
		G.connect(Gf,Gg);
		G.connect(Gg,Ge);
		
		G.connect(Gf,Gh);
		G.connect(Gg,Gi);
		G.connect(Gi,Gj);
		return G;
	}
	
	public static Graph H()
	{
		Graph H=new Graph();
		Node Ha=new Node(H,"Ha");
		H.addNode(Ha);
		Node Hb=new Node(H,"Hb");
		H.addNode(Hb);
		Node Hc=new Node(H,"Hc");
		H.addNode(Hc);
		Node Hd=new Node(H,"Hd");
		H.addNode(Hd);
		Node He=new Node(H,"He");
		H.addNode(He);
		
		H.connect(Ha,Hb);
		H.connect(Hb,Hc);
		H.connect(Hc,Ha);
		
		H.connect(Hb,Hd);
		H.connect(Hd,Hc);
		H.connect(Hd,He);
		H.connect(He,Hc);
		
		
		
		return H;
	}
	
	public static Graph I()
	{
		Graph I=new Graph();
		Node Ia=new Node(I,"Ia");
		I.addNode(Ia);
		Node Ib=new Node(I,"Ib");
		I.addNode(Ib);
		Node Ic=new Node(I,"Ic");
		I.addNode(Ic);
		Node Id=new Node(I,"Id");
		I.addNode(Id);
		
		I.connect(Ia,Ib);
		I.connect(Ib,Ic);
		I.connect(Ic,Id);
		
		return I;
	}
	
	public static Graph J()
	{
		Graph J=new Graph();
		Node Ja=new Node(J,"Ja");
		J.addNode(Ja);
		Node Jb=new Node(J,"Jb");
		J.addNode(Jb);
		Node Jc=new Node(J,"Jc");
		J.addNode(Jc);
		Node Jd=new Node(J,"Jd");
		J.addNode(Jd);
		Node Je=new Node(J,"Je");
		J.addNode(Je);
		
		J.connect(Ja,Jb);
		J.connect(Jb,Jc);
		J.connect(Jc,Jd);
		J.connect(Jd,Je);
		J.connect(Je,Ja);
		J.connect(Jb,Je);
		
		return J;
	}
	
	public static Graph K()
	{
		Graph K=new Graph();
		Node Ka=new Node(K,"Ka");
		K.addNode(Ka);
		Node Kb=new Node(K,"Kb");
		K.addNode(Kb);
		Node Kc=new Node(K,"Kc");
		K.addNode(Kc);
		Node Kd=new Node(K,"Kd");
		K.addNode(Kd);
		Node Ke=new Node(K,"Ke");
		K.addNode(Ke);
		Node Kf=new Node(K,"Kf");
		K.addNode(Kf);
		Node Kg=new Node(K,"Kg");
		K.addNode(Kg);
		
		K.connect(Ka,Kb);
		K.connect(Kb,Kc);
		K.connect(Kc,Ka);
		
		K.connect(Kc,Kd);		
		K.connect(Kd,Ke);
		
		K.connect(Ke,Kf);
		K.connect(Kf,Kg);
		K.connect(Kg,Ke);
		
		return K;
	}
	
	public static Graph L()
	{
		Graph l=new Graph();
		Node la=new Node(l,"la");
		Node lb=new Node(l,"lb");
		Node lc=new Node(l,"lc");
		Node ld=new Node(l,"ld");
		
		l.addNode(la);
		l.addNode(lb);
		l.addNode(lc);
		l.addNode(ld);
		
		l.connect(la,lb);
		l.connect(lb,lc);
		l.connect(ld,lc);
		
		return l;
	}
	
	public static Graph M()
	{
		Graph M=new Graph();
		Node Ma=new Node(M,"Ma");
		M.addNode(Ma);
		Node Mb=new Node(M,"Mb");
		M.addNode(Mb);
		Node Mc=new Node(M,"Mc");
		M.addNode(Mc);
		Node Md=new Node(M,"Md");
		M.addNode(Md);
		Node Me=new Node(M,"Me");
		M.addNode(Me);
		Node Mf=new Node(M,"Mf");
		M.addNode(Mf);
		
		M.connect(Ma, Mb);
		M.connect(Mb, Mc);
		M.connect(Mc, Mb);
		
		M.connect(Mc, Md);
		M.connect(Md, Mf);
		M.connect(Mf, Me);
		M.connect(Me, Md);
		
		
		return M;
	}
	
	public static Graph O()
	{
		Graph o=new Graph();
		Node oa=new Node(o,"oa");
		Node ob=new Node(o,"ob");
		Node oc=new Node(o,"oc");
		Node od=new Node(o,"od");
		Node oe=new Node(o,"oe");
		
		o.addNode(oa);
		o.addNode(ob);
		o.addNode(oc);
		o.addNode(od);
		o.addNode(oe);
		
		o.connect(oa,ob);
		o.connect(oa,oc);
		o.connect(ob,od);
		o.connect(oc,od);
		o.connect(od,oe);
		
		return o;
	}
	
	public static Graph P()
	{
		Graph p=new Graph();
		Node pa=new Node(p,"pa");
		Node pb=new Node(p,"pb");
		Node pc=new Node(p,"pc");
		Node pd=new Node(p,"pd");
		Node pe=new Node(p,"pe");
		Node pf=new Node(p,"pf");
		Node pg=new Node(p,"pg");
		Node ph=new Node(p,"ph");
		
		p.addNode(pa);
		p.addNode(pb);
		p.addNode(pc);
		p.addNode(pd);
		p.addNode(pe);
		p.addNode(pf);
		p.addNode(pg);
		p.addNode(ph);
		
		p.connect(pa,pd);
		p.connect(pb,pd);
		p.connect(pc,pe);
		p.connect(pd,pf);
		p.connect(pd,pg);
		p.connect(pe,ph);
		p.connect(pe,pg);
		return p;
	}
	
	
	
	public static Graph Y()
	{
		Graph y=new Graph();
		Node ya=new Node(y,"ya");
		Node yb=new Node(y,"yb");
		Node yc=new Node(y,"yc");
		Node yd=new Node(y,"yd");
		
		y.addNode(ya);
		y.addNode(yb);
		y.addNode(yc);
		y.addNode(yd);
		
		y.connect(ya,yc);
		y.connect(yb,yc);
		y.connect(yc,yd);
		
		return y;
	}
	
	public static Graph Cycle1()
	{
		Graph y=new Graph();
		Node y1=new Node(y,"y1");
		Node y2=new Node(y,"y2");
		Node y3=new Node(y,"y3");
		Node y4=new Node(y,"y4");
		Node y5=new Node(y,"y5");
		Node y6=new Node(y,"y6");
		Node y7=new Node(y,"y7");
		
		y.addNode(y1);
		y.addNode(y2);
		y.addNode(y3);
		y.addNode(y4);
		y.addNode(y5);
		y.addNode(y6);
		y.addNode(y7);
		
		y.connect(y1,y2);
		y.connect(y1,y3);
		y.connect(y2,y4);
		y.connect(y2,y5);
		y.connect(y3,y5);
		y.connect(y3,y6);
		y.connect(y3,y7);
		y.connect(y6,y2);
		y.connect(y7,y1);
		
		return y;
	}
	
	public static Graph CycleOfThree()
	{
		return A();	
	}
	
	public static Graph CycleOf_n(int n)
	{
		Graph g=new Graph();
		Node start=new Node(g,"0");
		g.addNode(start);
		Node prev=start;
		for(int i=1; i!= n; ++i)
		{
			Node node=new Node(g,""+i);
			g.addNode(node);
			g.connect(prev, node);
			prev=node;
		}
		g.connect(prev, start);
		return g;	
	}
	
	/**
	 * @param numberOfPoints
	 * @param toCenter true: point --> center, false: point 
	 * @return
	 */
	public static Graph star(int numberOfPoints, boolean toCenter)
	{
		Graph g=new Graph();
		Node center=new Node(g,"center");
		g.addNode(center);
		
		for(int i=0; i!= numberOfPoints; ++i)
		{
			Node n=new Node(g,Integer.toString(i));
			g.addNode(n);
			if(toCenter)
				g.connect(n, center);
			else
				g.connect(center,n);
		}
		
		return g;
	}
	
	public static Graph layeredGraph1()
	{
		Graph graph=new Graph();
		
		Node a=new Node(graph,"A");
		graph.addNode(a);
		Node b=new Node(graph,"B");
		graph.addNode(b);
		Node c=new Node(graph,"C");
		graph.addNode(c);
		Node d=new Node(graph,"D");
		graph.addNode(d);
		Node e=new Node(graph,"E");
		graph.addNode(e);
		Node f=new Node(graph,"F");
		graph.addNode(f);
		Node g=new Node(graph,"G");
		graph.addNode(g);
		Node h=new Node(graph,"H");
		graph.addNode(h);
		Node i=new Node(graph,"I");
		graph.addNode(i);
		Node j=new Node(graph,"J");
		graph.addNode(j);
		Node k=new Node(graph,"K");
		graph.addNode(k);
		Node l=new Node(graph,"L");
		graph.addNode(l);
		Node m=new Node(graph,"M");
		graph.addNode(m);
		Node n=new Node(graph,"N");
		graph.addNode(n);
		Node o=new Node(graph,"O");
		graph.addNode(o);

		graph.connect(a, e);
		
		graph.connect(b, e);
		graph.connect(b, j);
		
		graph.connect(c, i);
		graph.connect(c, f);
		graph.connect(c, g);
		
		graph.connect(d, g);
		
		graph.connect(e, h);
		graph.connect(e, i);
		
		graph.connect(f, j);
		graph.connect(f, k);
		
		graph.connect(g, k);
		graph.connect(g, o);
		
		graph.connect(h, a);
		graph.connect(h, l);
		
		graph.connect(i, l);
		graph.connect(i, m);
		graph.connect(i, n);
		
		graph.connect(j, n);
		
		graph.connect(k, n);
		
		graph.connect(n, f);
		return graph;
	}
	
	public static Graph layeredGraph2()
	{
		Graph graph=new Graph();
		
		Node a=new Node(graph,"1");
		graph.addNode(a);
		Node b=new Node(graph,"2");
		graph.addNode(b);
		Node c=new Node(graph,"3");
		graph.addNode(c);
		Node d=new Node(graph,"4");
		graph.addNode(d);
		Node e=new Node(graph,"5");
		graph.addNode(e);
		Node f=new Node(graph,"6");
		graph.addNode(f);
		Node g=new Node(graph,"7");
		graph.addNode(g);
		Node h=new Node(graph,"8");
		graph.addNode(h);
		Node i=new Node(graph,"9");
		graph.addNode(i);
		Node j=new Node(graph,"10");
		graph.addNode(j);
		Node k=new Node(graph,"11");
		graph.addNode(k);
		Node l=new Node(graph,"12");
		graph.addNode(l);
		Node m=new Node(graph,"13");
		graph.addNode(m);
		Node n=new Node(graph,"14");
		graph.addNode(n);
		Node o=new Node(graph,"0");
		graph.addNode(o);

		graph.connect(b, h);
		graph.connect(b, l);
		graph.connect(b, e);
		graph.connect(b, g);
		graph.connect(b, m);
		graph.connect(b, f);		
		graph.connect(c, h);
		graph.connect(c, l);		
		graph.connect(c, i);		
		graph.connect(f, k);
		graph.connect(f, m);		
		graph.connect(g, n);		
		graph.connect(d, j);
		graph.connect(d, n);
		graph.connect(d, l);		
		graph.connect(a, l);
		graph.connect(a, e);
		graph.connect(i, l);
		graph.connect(i, e);		
		graph.connect(o, e);
		graph.connect(o, m);		
		graph.connect(g, m);
		graph.connect(k, m);		
		graph.connect(j, n);		
		graph.connect(e, n);
		graph.connect(m, n);
		
		
		return graph;
	}
	
	public static Graph transitive3()
	{
		Graph y=new Graph();
		Node ya=new Node(y,"ya");
		Node yb=new Node(y,"yb");
		Node yc=new Node(y,"yc");
		
		y.addNode(ya);
		y.addNode(yb);
		y.addNode(yc);
		
		y.connect(ya,yb);
		y.connect(yb,yc);
		y.connect(ya,yc);
		
		return y;
	}
	
	public static Graph sampleTree()
	{
		Graph g=new Graph();
		Node n01=new Node(g,"0");
		g.addNode(n01);
		
		Node n11=new Node(g,"1");
		g.addNode(n11);
		Node n12=new Node(g,"2");
		g.addNode(n12);
		Node n13=new Node(g,"3");
		g.addNode(n13);
		Node n14=new Node(g,"4");
		g.addNode(n14);
		
		Node n15=new Node(g,"5");
		g.addNode(n15);
		
		g.connect(n01, n11);
		g.connect(n01, n12);
		g.connect(n01, n13);
		g.connect(n01, n14);
		g.connect(n01, n15);
		
		Node n21=new Node(g,"11");
		g.addNode(n21);
		Node n22=new Node(g,"12");
		g.addNode(n22);
		Node n23=new Node(g,"13");
		g.addNode(n23);
		Node n24=new Node(g,"14");
		g.addNode(n24);
		
		g.connect(n11, n21);
		g.connect(n11, n22);
		g.connect(n11, n23);
		g.connect(n11, n24);
		
		Node n23a=new Node(g,"13a");
		g.addNode(n23a);
		Node n24a=new Node(g,"13b");
		g.addNode(n24a);
		
		
		
		g.connect(n23, n23a);
		g.connect(n23, n24a);
		
		
		Node n25=new Node(g,"21");
		g.addNode(n25);
		Node n26=new Node(g,"22");
		g.addNode(n26);
		Node n27=new Node(g,"23");
		g.addNode(n27);
		
		g.connect(n12, n25);
		g.connect(n12, n26);
		g.connect(n12, n27);
		
		Node n28=new Node(g,"31");
		g.addNode(n28);
		Node n29=new Node(g,"32");
		g.addNode(n29);
		
		g.connect(n13, n28);
		g.connect(n13, n29);

		Node n210=new Node(g,"41");
		g.addNode(n210);
		Node n211=new Node(g,"42");
		g.addNode(n211);
		
		g.connect(n14, n210);
		g.connect(n14, n211);
		
		return g;
		
	}
	
	public static Graph rootedTree(int layers, int rootDegree, int degree)
	{
		Graph t=new Graph();
		Node root=new Node(t,"root");
		t.addNode(root);
		for(int i=0; i!= rootDegree; ++i)
		{
			Node n=new Node(t);
			t.addNode(n);
			t.connect(root,n);
			layer(t, n, layers-2, degree);
		}
		
		return t;
	}
	
	public static Graph rootedTree(int layers, int degree)
	{
		Graph t=new Graph();
		Node root=new Node(t,"root");
		t.addNode(root);
		layer(t, root, layers-1, degree);
		return t;
	}

	private static void layer(Graph t, Node root, int layer, int degree) 
	{
		if(layer==0)
			return;
		for(int i=0; i!= degree; ++i)
		{
			Node n=new Node(t);
			n.setName(""+layer+"-"+i);
			t.addNode(n);
			t.connect(root,n);
			layer(t,n,layer-1,degree);
		}		
	}
	
	public static Graph cliqueExample()
	{
		Graph o=new Graph();
		Node o1=new Node(o,"o1");
		Node o2=new Node(o,"o2");
		Node o3=new Node(o,"o3");
		Node o4=new Node(o,"o4");
		Node o5=new Node(o,"o5");
		Node o6=new Node(o,"o6");
		
		o.addNode(o1);
		o.addNode(o2);
		o.addNode(o3);
		o.addNode(o4);
		o.addNode(o5);
		o.addNode(o6);
		
		o.connect(o1,o2);
		o.connect(o1,o5);
		o.connect(o5,o2);
		
		o.connect(o4,o5);
		o.connect(o3,o2);
		o.connect(o4,o3);
		o.connect(o4,o6);
		
		
		
		return o;
	}
	
	public static class HierarchicalExamples
	{
		public static Graph A1()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"a1a");
			Node o1b=new Node(o,"a1b");
			Node o1c=new Node(o,"a1c");
			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.connect(o1a,o1b);
			o.connect(o1a,o1c);
			
			Node o1d=new Node(o,"a1d");
			Node o1e=new Node(o,"a1e");
			o.addNode(o1d);
			o.addNode(o1e);
			o.connect(o1b,o1d);
			o.connect(o1b,o1e);
			
			Node o1f=new Node(o,"a1f");
			Node o1g=new Node(o,"a1g");
			Node o1h=new Node(o,"a1h");
			Node o1i=new Node(o,"a1i");
			o.addNode(o1f);
			o.addNode(o1g);
			o.addNode(o1h);
			o.addNode(o1i);
			o.connect(o1c,o1f);
			o.connect(o1c,o1g);
			o.connect(o1c,o1h);
			o.connect(o1c,o1i);
			return o;
		}
		
		public static Graph A2()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"a2a");
			Node o1b=new Node(o,"a2b");
			Node o1c=new Node(o,"a2c");
			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.connect(o1a,o1b);
			o.connect(o1b,o1c);
			
			Node o1d=new Node(o,"a2d");
			Node o1e=new Node(o,"a2e");
			Node o1f=new Node(o,"a2f");
			o.addNode(o1d);
			o.addNode(o1e);
			o.addNode(o1f);
			o.connect(o1c,o1d);
			o.connect(o1c,o1e);
			o.connect(o1c,o1f);
			return o;
		}
		
		public static Graph A()
		{
			Graph a=new Graph();
			a.add(A1());
			a.add(A2());
			return a;
		}
		
		public static Graph B()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"b1a");
			Node o1b=new Node(o,"b1b");
			Node o1c=new Node(o,"b1c");
			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.connect(o1a,o1b);
			o.connect(o1a,o1c);
			
			Node o1d=new Node(o,"b1d");
			Node o1e=new Node(o,"b1e");
			o.addNode(o1d);
			o.addNode(o1e);
			o.connect(o1b,o1d);
			o.connect(o1b,o1e);
			return o;
		}
		
		public static Graph C()
		{
			Graph a=new Graph();
			a.add(A());
			a.add(B());
			return a;
		}
		
		public static Graph D()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"b1a");
			Node o1b=new Node(o,"b1b");
			o.addNode(o1a);
			o.addNode(o1b);
			
			Node o1c=new Node(o,"b1c");
			Node o1d=new Node(o,"b1d");
			o.addNode(o1c);
			o.addNode(o1d);
			o.connect(o1a,o1b);
			o.connect(o1b,o1c);
			o.connect(o1b,o1d);
			
			Node o1e=new Node(o,"b1e");
			Node o1f=new Node(o,"b1f");
			Node o1g=new Node(o,"b1g");
			o.addNode(o1e);
			o.addNode(o1f);
			o.addNode(o1g);
			o.connect(o1c,o1e);
			o.connect(o1c,o1f);
			o.connect(o1d,o1f);
			o.connect(o1d,o1g);
			return o;
		}
		
		public static Graph E()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"a2a");			
			Node o1b=new Node(o,"a2b");
			Node o1c=new Node(o,"a2c");
			Node o1d=new Node(o,"a2d");
			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.addNode(o1d);			
			o.connect(o1a,o1b);
			o.connect(o1a,o1c);
			o.connect(o1a,o1d);
			
			// children of b
			Node o1e=new Node(o,"a2e");
			Node o1f=new Node(o,"a2f");
			Node o1g=new Node(o,"a2g");
			o.addNode(o1e);
			o.addNode(o1f);
			o.addNode(o1g);
			o.connect(o1b,o1e);
			o.connect(o1b,o1f);
			o.connect(o1b,o1g);
			// children of c
			Node o1h=new Node(o,"a2h");
			o.addNode(o1h);
			o.connect(o1c,o1h);
			// children of d
			Node o1i=new Node(o,"a2i");
			Node o1j=new Node(o,"a2j");
			Node o1k=new Node(o,"a2k");
			o.addNode(o1i);
			o.addNode(o1j);
			o.addNode(o1k);
			o.connect(o1d,o1i);
			o.connect(o1d,o1j);
			o.connect(o1d,o1k);
			// children of e,f,g
			Node o1l=new Node(o,"a2l");
			Node o1m=new Node(o,"a2m");
			Node o1n=new Node(o,"a2n");
			o.addNode(o1l);
			o.addNode(o1m);
			o.addNode(o1n);
			o.connect(o1e,o1l);
			o.connect(o1e,o1m);
			o.connect(o1f,o1m);
			o.connect(o1g,o1n);
			// children of h
			Node o1p=new Node(o,"a2p");
			o.addNode(o1p);
			o.connect(o1h,o1p);
			// children of i,j,k
			Node o1q=new Node(o,"a2q");
			Node o1r=new Node(o,"a2r");
			Node o1s=new Node(o,"a2s");
			Node o1t=new Node(o,"a2t");
			Node o1u=new Node(o,"a2u");
			o.addNode(o1q);
			o.addNode(o1r);
			o.addNode(o1s);
			o.addNode(o1t);
			o.addNode(o1u);
			o.connect(o1i,o1q);
			o.connect(o1i,o1r);
			o.connect(o1j,o1s);
			o.connect(o1k,o1t);
			o.connect(o1k,o1u);
			return o;
			
		}
		
		public static Graph F()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"b1a");
			Node o1b=new Node(o,"b1b");
			Node o1c=new Node(o,"b1c");
			Node o1d=new Node(o,"b1d");
			Node o1e=new Node(o,"b1e");
			Node o1f=new Node(o,"b1f");
			Node o1g=new Node(o,"b1g");
			Node o1h=new Node(o,"b1h");
			Node o1i=new Node(o,"b1i");
			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.addNode(o1d);
			o.addNode(o1e);
			o.addNode(o1f);
			o.addNode(o1g);
			o.addNode(o1h);
			o.addNode(o1i);
			
			o.connect(o1a,o1d);
			o.connect(o1a,o1e);
			o.connect(o1b,o1e);
			o.connect(o1c,o1e);
			o.connect(o1c,o1f);
			
			o.connect(o1d,o1g);
			o.connect(o1d,o1h);
			o.connect(o1e,o1h);
			o.connect(o1e,o1i);
			o.connect(o1f,o1i);
			return o;
		}
		
	}
	
	public static class CycleRemovalExamples
	{
		public static Graph C()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"b1a");
			Node o1b=new Node(o,"b1b");
			o.addNode(o1a);
			o.addNode(o1b);
			o.connect(o1a,o1b);
			o.connect(o1b,o1a);
			return o;
		}
		
		public static Graph C2()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"b1a");
			Node o1b=new Node(o,"b1b");
			o.addNode(o1a);
			o.addNode(o1b);
			o.connect(o1a,o1b);
			o.connect(o1a,o1b);
			return o;
		}
		
		public  static Graph A_Tree()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"a1a");
			Node o1b=new Node(o,"a1b");
			Node o1c=new Node(o,"a1c");
			Node o1d=new Node(o,"a1d");
			Node o1e=new Node(o,"a1e");
			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.addNode(o1d);
			o.addNode(o1e);

			o.connect(o1a,o1b);
			o.connect(o1b,o1c);
			o.connect(o1c,o1d);
			o.connect(o1c,o1e);
			return o;
		}
		
		public static  Graph A_TwoCycle()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"a1a");
			Node o1b=new Node(o,"a1b");
			Node o1c=new Node(o,"a1c");
			Node o1d=new Node(o,"a1d");
			Node o1e=new Node(o,"a1e");
			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.addNode(o1d);
			o.addNode(o1e);

			o.connect(o1a,o1b);
			o.connect(o1b,o1c);
			o.connect(o1c,o1d);
			o.connect(o1c,o1e);
			
			o.connect(o1b,o1a);
			return o;
		}
		
		public static Graph A_TwoCycle_2()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"a1a");
			Node o1b=new Node(o,"a1b");
			Node o1c=new Node(o,"a1c");
			Node o1d=new Node(o,"a1d");
			Node o1e=new Node(o,"a1e");
			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.addNode(o1d);
			o.addNode(o1e);

			o.connect(o1a,o1b);
			o.connect(o1b,o1c);
			o.connect(o1c,o1d);
			o.connect(o1c,o1e);
			
			o.connect(o1c,o1b);
			return o;
		}
		
		public static Graph A_ThreeCycle()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"a1a");
			Node o1b=new Node(o,"a1b");
			Node o1c=new Node(o,"a1c");
			Node o1d=new Node(o,"a1d");
			Node o1e=new Node(o,"a1e");
			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.addNode(o1d);
			o.addNode(o1e);

			o.connect(o1a,o1b);
			o.connect(o1b,o1c);
			o.connect(o1c,o1d);
			o.connect(o1c,o1e);
			
			o.connect(o1c,o1a);
			return o;
		}
		
		public  static Graph A_Round()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"a1a");
			Node o1b=new Node(o,"a1b");
			Node o1c=new Node(o,"a1c");
			Node o1d=new Node(o,"a1d");
			Node o1e=new Node(o,"a1e");
			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.addNode(o1d);
			o.addNode(o1e);

			o.connect(o1a,o1b);
			o.connect(o1b,o1c);
			o.connect(o1c,o1d);
			o.connect(o1c,o1e);
			
			o.connect(o1d,o1a);
			o.connect(o1e,o1a);
			return o;
		}
		
		public static Graph A_Pin()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"a1a");
			Node o1b=new Node(o,"a1b");
			Node o1c=new Node(o,"a1c");
			Node o1d=new Node(o,"a1d");
			Node o1e=new Node(o,"a1e");
			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.addNode(o1d);
			o.addNode(o1e);

			o.connect(o1a,o1b);
			o.connect(o1b,o1c);
			o.connect(o1c,o1d);
			o.connect(o1c,o1e);
			
			o.connect(o1e,o1d);
			return o;
		}
		
		
		public static Graph A_Pin_2()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"a1a");
			Node o1b=new Node(o,"a1b");
			Node o1c=new Node(o,"a1c");
			Node o1d=new Node(o,"a1d");
			Node o1e=new Node(o,"a1e");
			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.addNode(o1d);
			o.addNode(o1e);

			o.connect(o1a,o1b);
			o.connect(o1b,o1c);
			o.connect(o1c,o1d);
			o.connect(o1c,o1e);
			
			o.connect(o1e,o1d);
			o.connect(o1d,o1b);
			return o;
		}
		
		public static Graph B_1()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"b1a");
			Node o1b=new Node(o,"b1b");
			Node o1c=new Node(o,"b1c");
			Node o1d=new Node(o,"b1d");
			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.addNode(o1d);

			o.connect(o1a,o1b);
			o.connect(o1b,o1c);
			o.connect(o1c,o1d);
						
			o.connect(o1a,o1c);
			o.connect(o1b,o1d);
			return o;
		}
		
		public static Graph B_2()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"b1a");
			Node o1b=new Node(o,"b1b");
			Node o1c=new Node(o,"b1c");
			Node o1d=new Node(o,"b1d");
			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.addNode(o1d);

			o.connect(o1a,o1b);
			o.connect(o1b,o1c);
			o.connect(o1c,o1d);
						
			o.connect(o1a,o1c);
			o.connect(o1d,o1b);
			return o;
		}		
	}
	
	public static class TransitiveReductionExamples
	{
		public static Graph A()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"A");
			Node o1b=new Node(o,"B");
			Node o1c=new Node(o,"C");
			Node o1d=new Node(o,"D");
			Node o1e=new Node(o,"E");

			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.addNode(o1d);
			o.addNode(o1e);

			o.connect(o1a,o1b);
			o.connect(o1a,o1c);
			o.connect(o1a,o1d);
			o.connect(o1b,o1d);
			o.connect(o1c,o1d);
			o.connect(o1d,o1e);
			o.connect(o1c,o1e);
			o.connect(o1a,o1e);
			return o;
		}
		
		public static Graph B()
		{
			Graph o=new Graph();
			Node o1a=new Node(o,"A");
			Node o1b=new Node(o,"B");
			Node o1c=new Node(o,"C");
			Node o1d=new Node(o,"D");
			Node o1e=new Node(o,"E");
			Node o1f=new Node(o,"F");

			o.addNode(o1a);
			o.addNode(o1b);
			o.addNode(o1c);
			o.addNode(o1d);
			o.addNode(o1e);
			o.addNode(o1f);

			o.connect(o1a,o1b);
			o.connect(o1a,o1c);
			o.connect(o1a,o1d)
			;
			o.connect(o1b,o1d);
			o.connect(o1c,o1d);
			
			o.connect(o1d,o1e);
			o.connect(o1e,o1f);
			o.connect(o1a,o1f);
			o.connect(o1b,o1f);
			return o;
		}
		

	}
	
	
	public static void main(String[] args) 
	{
//		System.out.println(Graphs.transitiveReduction(TransitiveReductionExamples.A()).getEdges());
		System.out.println(TransitiveReductionExamples.B().getEdges());
		System.out.println(Graphs.transitiveReduction(TransitiveReductionExamples.B()).getEdges());
	}

	
}
