/* 
 * Kodkod -- Copyright (c) 2005-present, Emina Torlak
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package kodkod.engine.satlab;

import java.util.Iterator;

import kodkod.util.ints.IntSet;


/**
 * <p>A proof of unsatisfiability generated by a {@linkplain SATProver}.
 * Formally, a resolution trace is a sequence of inferences made by a  
 * prover that ends in a conflict (empty clause).  An element in a resolution
 * trace is called a clause.  There are two kinds of clauses in the trace: 
 * axioms and resolvents.  Axioms are the clauses given to the 
 * prover via the {@linkplain SATSolver#addClause(int[])} method, and resolvents are  
 * the clauses derived by the prover from axioms or previously learned resolvents through 
 * <a href="http://en.wikipedia.org/wiki/Resolution_%28logic%29">resolution</a>.</p>
 * 
 * <p>Clauses in a resolution trace are ordered as follows.  The first |<i>A</i>| elements
 * in the trace correspond to the axioms given to the prover (i.e. prover.clauses).  
 * An axiom <i>a1</i> precedes an axiom <i>a2</i> in the trace if and only if 
 * <i>a1</i> was added to the prover before <i>a2</i>.  (An axiom is "added" to the 
 * prover, and appears in the trace, if and only if the corresponding call to {@linkplain SATSolver#addClause(int[])})
 * returned <tt>true</tt>.)  The remaining elements in the trace
 * are the resolvents.  A resolvent <i>r</i> succeeds all of the resolvents needed for its derivation
 * (i.e. all resolvents reachable from r via the {@linkplain Clause#antecedents()} relation).  
 * The last element in the trace is the conflict resolvent.  The axioms that are reachable from the conflict
 * form the <i>unsatisfiable core</i> of the trace. <p>
 *  
 * @specfield prover: SATProver
 * @specfield elts: Clause[]
 * @specfield conflict: elts[#elts-1]
 * @invariant #elts = #(prover.clauses + prover.resolvents)
 * @invariant elts[[0..#prover.clauses)].literals = prover.clauses
 * @invariant elts[[#prover.clauses..#elts)].literals = prover.resolvents
 * @invariant all i: [0..#prover.clauses) | no elts[i].antecedents 
 * @invariant all i: [#prover.clauses..#elts) | all ante: elts[i].antecedents[int] | elts.ante < i
 * @invariant no conflict.literals
 * 
 * @author Emina Torlak
 */
public interface ResolutionTrace extends Iterable<Clause> {

	/**
	 * Returns the length of this trace.
	 * @return #this.elts
	 */
	public int size();
		
	/**
	 * Returns an iterator over the elements in this trace  in proper sequence.
	 * <p><b>Note:</b>The clause objects returned by the iterator are not 
	 * required to be immutable. In particular, the state of a clause object 
	 * returned by <tt>next()</tt> (as well as the state of any object obtained
	 * through that clause's {@linkplain Clause#antecedents()} method) is guaranteed 
	 * to remain the same only until the subsequent call to the <tt>next()</tt> method 
	 * of the iterator instance.</p>
	 * @return an iterator over the elements in this trace in proper sequence.
	 */
	public abstract Iterator<Clause> iterator();
	
	/**
	 * Returns an iterator over the elements at the given indices in this trace, in proper sequence.
	 * <p><b>Note:</b>The clause objects returned by the iterator are not 
	 * required to be immutable. In particular, the state of a clause object 
	 * returned by <tt>next()</tt> (as well as the state of any object obtained
	 * through that clause's {@linkplain Clause#antecedents()} method) is guaranteed 
	 * to remain the same only until the subsequent call to the <tt>next()</tt> method 
	 * of the iterator instance.</p>
	 * @requires indices.min() >= 0 && indices.max() < this.size()
	 * @return an iterator over the elements at the given indices in this trace, in proper sequence.
	 * @throws IndexOutOfBoundsException  indices.min() < 0 || indices.max() >= this.size()
	 */
	public abstract Iterator<Clause> iterator(IntSet indices);
	
	/**
	 * Returns an iterator over the elements at the given indices in this trace, in the 
	 * reverse order of indices.
	 * <p><b>Note:</b>The clause objects returned by the iterator are not 
	 * required to be immutable. In particular, the state of a clause object 
	 * returned by <tt>next()</tt> (as well as the state of any object obtained
	 * through that clause's {@linkplain Clause#antecedents()} method) is guaranteed 
	 * to remain the same only until the subsequent call to the <tt>next()</tt> method 
	 * of the iterator instance.</p>
	 * @requires indices.min() >= 0 && indices.max() < this.size()
	 * @return an iterator over the elements at the given indices in this trace, in the 
	 * reverse order of indices.
	 * @throws IndexOutOfBoundsException  indices.min() < 0 || indices.max() >= this.size()
	 */
	public abstract Iterator<Clause> reverseIterator(IntSet indices);
	
	/**
	 * Returns the indices of the axioms that form the unsatisfiable core of this trace.
	 * @return { i: int | no this.elts[i].antecedents and this.elts[i] in this.conflict.^antecedents }
	 */
	public abstract IntSet core();
		
	/**
	 * Returns the indices of the axioms in this trace.
	 * @return { i: int | this.elts[i] in this.prover.clauses }
	 */
	public abstract IntSet axioms(); 
	
	/**
	 * Returns the indices of the resolvents in this trace.
	 * @return { i: int | this.elts[i] in this.prover.resolvents }
	 */
	public abstract IntSet resolvents();
	
	/**
	 * Returns  the indices of all clauses reachable from the clauses at the given indices 
	 * by following the antecedent relation zero or more times.
	 * @requires indices.min() >= 0 && indices.max() < this.size()
	 * @return { i: int | this.elts[i] in this.elts[indices].*antecedents }
	 * @throws IllegalArgumentException  indices.min() < 0 || indices.max() >= this.size()
	 */
	public abstract IntSet reachable(IntSet indices);
	
	/**
	 * Returns the indices of all clauses reachable from the clauses at the given indices 
	 * by following the transpose of the antecedent relation zero or more times.
	 * @requires indices.min() >= 0 && indices.max() < this.size()
	 * @return { i: int | this.elts[i] in this.elts[indices].*~antecedents }
	 * @throws IllegalArgumentException  indices.min() < 0 || indices.max() >= this.size()
	 */
	public abstract IntSet backwardReachable(IntSet indices);
	
	/**
	 * Returns the indices of all clauses in this trace that can be learned solely from the
	 * clauses with the given indices.
	 * @requires indices.min() >= 0 && indices.max() < this.size()
	 * @return { i: int | this.elts[i].*antecedents = this.elts[indices].*antecedents + this.elts[i].*antecedents & this.elts[indices].*~antecedents }
	 * @throws IllegalArgumentException  indices.min() < 0 || indices.max() >= this.size()
	 */
	public abstract IntSet learnable(IntSet indices);
	
	/**
	 * Returns the indices of all clauses in this trace that can be learned solely and directly from the
	 * clauses with the given indices.
	 * @requires indices.min() >= 0 && indices.max() < this.size()
	 * @return { i: int | this.elts[i].antecedents in this.elts[indices] }
	 * @throws IllegalArgumentException  indices.min() < 0 || indices.max() >= this.size()
	 */
	public abstract IntSet directlyLearnable(IntSet indices);
	
	
	/**
	 * Returns the clause at the given index.  Note that this method is not required 
	 * to always return the same Clause object; it is only required to return Clause
	 * objects that are equal according to their <tt>equals</tt> methods.  The Clause
	 * objects returned by this method are guaranteed to be immutable.
	 * @requires 0 <= index < this.size()
	 * @return this.elts[index]
	 * @throws IndexOutOfBoundsException  0 < index || index >= this.size()
	 */
	public abstract Clause get(int index);
	
}
