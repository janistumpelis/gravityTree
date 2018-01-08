//Janis Tumpelis 
//This code is an example for solution of problem mentioned in the following link
//https://www.hackerrank.com/contests/w23/challenges/gravity-1/problem


def calculateAndPrintOutSolution(Scanner sc){
	Vertex[] allNodes = populateTree(sc)
	
	def testCaseCount = sc.nextInt()
	(testCaseCount..1).each{
		def u = sc.nextInt()
		def v = sc.nextInt()
		println allNodes[u].calculateForceFromNode(allNodes[v])
	}
}

def populateTree(sc){
	def nodeCount = sc.nextInt()
	
	Vertex[] allNodes = new Vertex[nodeCount + 1]
	allNodes[1] = new Vertex(label: 1)
	
	def iterateIndexes = getIndexIterator(nodeCount)
	iterateIndexes{allNodes[it] = new Vertex(label: it)}
	iterateIndexes{ 
		def x = sc.nextInt()
		allNodes[x].addChild(allNodes[it])
	}
	allNodes[1].computeCoefficients()
	return allNodes
}

def getIndexIterator(nodeCount){
	return {closure -> 
		(2..nodeCount).each{index -> closure.call(index)}
	}
}

class Vertex {
	
	int label
	int level
	
	def children = []
	Vertex parent = null
	long dCoef = 0
	long d2Coef = 0
	int descendantCount = 0
	
	def addChild(vertex) {
		children.add(vertex)
		vertex.parent = this
	}
	
	def computeCoefficients(){
		level = parent != null ? parent.level + 1 : 0
		if (children.size != 0) {
			setCoefficientsForMultiNodeVertex()
		} else {
			setCoefficientsForSingleVertex()
		}
	}
	
	long calculateForceFromNode(vertex) {
		if (findCommonAncestor(vertex).label == vertex.label) {
			return sumSquaresWhenGivenVertexIsWithHigherLevel(vertex)
		} else {
			return vertex.getForceWithDescendantRate(calculateDistance(vertex))
		}
	}
	
	def setCoefficientsForMultiNodeVertex(){
		def tmpd = 0
		def tmpd2 = 0
		def tmpDescendantCount = 0
		
		iterateChildren{ child ->
			child.computeCoefficients()
			tmpd += child.dCoef + child.descendantCount + 1
			tmpDescendantCount += child.descendantCount + 1
			tmpd2 += child.d2Coef + 2 * child.dCoef + child.descendantCount + 1
		}
		
		dCoef = tmpd
		descendantCount = tmpDescendantCount
		d2Coef = tmpd2
	}
	
	def setCoefficientsForSingleVertex(){
		dCoef = 0
		d2Coef = 0
		descendantCount = 0
	}
	
	long sumSquaresWhenGivenVertexIsWithHigherLevel(vertex){
		def result = 0
		iterateChildren{child -> result += child.getForce(child.descendantCount + 1)}
		
		if (vertex.label != label) {
			def currentParent = parent
			def prev = this
			def distance = 1
			
			while (true) {
				result += (distance ** 2)
				result += currentParent.getChildrenForceSum(distance + 1, prev.label)
				
				if (currentParent.label == vertex.label) {
					break
				}
				prev = currentParent
				currentParent = currentParent.parent
				distance++
			}
		}
		return result
	}
	
	Vertex findCommonAncestor(vertex) {
		def firstVertex = this.level > vertex.level ? this : vertex
		def secondVertex = this.level > vertex.level ? vertex : this
		
		while (firstVertex.level > secondVertex.level) {
			firstVertex = firstVertex.parent
		}
		while (firstVertex.label != secondVertex.label) {
			firstVertex = firstVertex.parent
			secondVertex = secondVertex.parent
        }
		return firstVertex
	}
	
	int calculateDistance(vertex) {
		def v = findCommonAncestor(vertex)
		level + vertex.level - (v.level * 2)
	}
	
	long getChildrenForceSum(distance, labelToSkip){
		def result = 0 
		iterateChildren{ child ->
			if(child.label != labelToSkip){
				result += child.getForceWithDescendantRate(distance)
			}
		}
		return result
	}
	
	def iterateChildren(closure){
		children.each{closure.call(it)}
	}
	
	long getForce(distance){
		d2Coef + dCoef * 2 + distance
	}
	
	long getForceWithDescendantRate(distance){
		d2Coef + dCoef * 2 * distance + (descendantCount + 1) * distance ** 2
	}
}

calculateAndPrintOutSolution(new Scanner(System.in))