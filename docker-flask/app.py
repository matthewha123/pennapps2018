from flask import Flask, request, jsonify
import json

import kdtree
from gif_item import Gif_Item

google_key = 'AIzaSyDeTTFBpWHQddFBElOInfLhDd5Yw7rCf8g'

app = Flask(__name__)

with open('gif_data.json') as f:
	stored_dict_from_txt = json.load(f)

points = [Gif_Item(tuple(stored_dict_from_txt[name]['avg_rgb']), name) for name in stored_dict_from_txt.keys()]

root = kdtree.create(points)

def get_nearest_gifs(rgb, stored_dict, num_gifs):
	'''
	rgb: tuple containing the specified rgb value to match gifs to
	'''

	'''
	possible future improvements: 
		- eliminate already chosen images or eliminate them when chosen multiple times
		- Start choosing images at a more neutral point (50% brightness)
		- advice of: http://blog.wolfram.com/2008/05/02/making-photo-mosaics/
	'''

	output_gifs = root.search_knn(rgb, num_gifs, dist=None)
	output = [[str(gif[0].data), int(gif[1])] for gif in output_gifs]
	return output
	# print(output)
	# output_gifs = set()

	# for gif_name in stored_dict.keys():

	# 	distance = get_dist(rgb, stored_dict[gif_name]['avg_rgb'])
	# 	if len(output_gifs) < num_gifs:
	# 		output_gifs.add((gif_name, distance))
	# 	else:

	# 		worst_gif = max(output_gifs, key = lambda tup: tup[1])

	# 		if worst_gif[1] > distance:
	# 			output_gifs.remove(worst_gif)
	# 			output_gifs.add((gif_name, distance))

	# return output_gifs

def get_dist(target_rgb, ref_rgb):
	'''
	function that returns the "distance" between two rgb values
	room for optimization: weight the different colors differently for the human eye preference
	'''
	diff_rgb = [target_rgb[i] - ref_rgb[i] for i in range(3)]
	dist = 0
	for num in diff_rgb:
		dist += num**2
	return dist



@app.route('/')
def hello_world():
    """Print 'Hello, world!' as the response body."""
    return "I am nichols your lifelong rival Mr.Cheeseman"

@app.route('/api/nearest_gifs', methods=["GET"])
def return_nearest_gifs():

	target_rgb = [int(num_string) for num_string in request.args.get('rgb', '').split(',')]
	num_gifs_to_return = int(request.args.get('numgifs', ''))
	print('Target RGB: ', target_rgb)
	
	names_of_nearest_gifs = get_nearest_gifs(target_rgb, stored_dict_from_txt, num_gifs_to_return)
	# print('Nearest gifs: ', names_of_nearest_gifs)

	return jsonify(list(names_of_nearest_gifs))





if __name__ == '__main__':
    app.run('0.0.0.0', port=80, debug=True)
