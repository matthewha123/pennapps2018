public enum Quad{NW, NE, SW, SE};
    
    public Quad findQuad(int x, int y) {
    	Quad q = null;
    	if(x > (n / 2) && y > (n / 2)) {
    		q = Quad.SE;
    	}else if(x < (n / 2) && y > (n / 2)) {
    		q = Quad.SW;
    	}else if(x > (n / 2) && y < (n / 2)) {
    		q = Quad.NE;
    	}else if(x < (n / 2) && y < (n / 2)) {
    		q = Quad.NW;
    	}else {
    		System.out.println("Couldn't find quad");
    	}
    	return q;
    }
    
    public int[] moveGraphicCtx(Quad q) {
    	//returns the cell of the top left giph in the quad
    	int[] coor = new int[2];
    	//x coord is the first cell
    	switch(q) {
    	case SE: coor[0] = n / 2; coor[1] = n / 2; 
    		break;
		case NE: coor[0] = 0; coor[1] = n / 2;
			break;
		case NW: coor[0] = 0; coor[1] = 0;
			break;
		case SW: coor[0] = n / 2; coor[1] = 0;
			break;
		default:
			break;
    	}
    		return coor;
    }