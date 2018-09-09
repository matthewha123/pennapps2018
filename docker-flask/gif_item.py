class Gif_Item(object):
	def __init__(self, coords, data):
		self.coords = coords
		self.data = data

	def __len__(self):
		return len(self.coords)

	def __getitem__(self, i):
		return self.coords[i]

	def __repr__(self):
		return self.data
		#return 'Gif Item({}, {}, {}, {})'.format(self.coords[0], self.coords[1], self.coords[2], self.data)