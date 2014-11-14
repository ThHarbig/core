package mayday.vis3.graph.edges.strokes;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;

/*
	Copyright 2006 Jerry Huxtable

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */

public class HighlightStroke implements Stroke
{
		private Stroke edgeStroke;
		private Stroke highlight=new BasicStroke(3);

		public HighlightStroke( Stroke stroke) 
		{
			edgeStroke=stroke;
			
		}

		public Shape createStrokedShape( Shape shape ) {
			return highlight.createStrokedShape( edgeStroke.createStrokedShape( shape ) );
		}
	
}
