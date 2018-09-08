#giphy api key

giphyKey = "TSIIIOcIx5kqFH1hQ4LIaLo4zvId3SBk"

import urllib,json
import urllib.request

from PIL import Image, ImageSequence

from statistics import mean

import numpy as np

data = json.loads(urllib.request.urlopen("http://api.giphy.com/v1/gifs/search?q=green&api_key="+giphyKey+"&limit=25").read())
formatted_data = json.dumps(data, sort_keys=True, indent=4)


gifs = data['data']




num_gifs = 0
def process_gifs(search_query, gif_format_to_use, stored_dict):
	'''

	'''

	global num_gifs


	data = json.loads(urllib.request.urlopen("http://api.giphy.com/v1/gifs/search?q="+search_query+"&api_key="+giphyKey+"&limit=25").read())
	gifs = data['data']

	for gif in gifs:
		# print(gif['images'][gif_format_to_use])

		gif_file_name = gif['slug']+".gif"
		gif_url = gif['images'][gif_format_to_use]['url']
		urllib.request.urlretrieve(gif_url, gif_file_name)

		resize_and_save(gif_file_name, (50,50))

		avg_rgb = iterate_and_averagecolor(gif_file_name)
		num_gifs+= 1
		print('Final: ', avg_rgb, gif_file_name, num_gifs)
		stored_dict[gif_file_name] = {"url":gif_url, "avg_rgb": avg_rgb}

	return stored_dict


def write_to_text(dictionary):
	with open('gif_data.json', 'a') as gif_data:
		json.dump(dictionary, gif_data, indent = 4)


def iterate_and_averagecolor(gif_file_name):

	#schema: filename: {rgb: (num, num, num), }

	gif_img = Image.open(gif_file_name)
	

	curr_frame = 0

	num_frames_to_check = 10

	final_average = [0,0,0]
	while(curr_frame<num_frames_to_check):
		if curr_frame > gif_img.n_frames-1:
			break
		gif_img.seek(curr_frame)
		to_rgb = gif_img.convert(mode="RGB")
		average_rgb = getAverageRGBN(to_rgb)
		final_average = [final_average[i] + average_rgb[i] for i in range(3)]
		curr_frame+= 1
	return [int(final_average[i]/min(curr_frame, num_frames_to_check)) for i in range(3)]


def getAverageRGBN(image):
  """
  Given PIL Image, return average value of color as (r, g, b)
  """
  # get image as numpy array
  im = np.array(image)
  # get shape
  w,h,d = im.shape
  # change shape
  im.shape = (w*h, d)
  # get average
  return tuple(im.mean(axis=0))



def resize_and_save(gif_file_name, size):
	'''
	gif_file_name: string, specifying the filename of the gif you want to resize
	size: tuple of 2 nonnegative integers specifying the desired output size
	'''

	im = Image.open(gif_file_name)

	frames = ImageSequence.Iterator(im)
	def thumbnails(frames):
		for frame in frames:
			thumbnail = frame.copy()
			thumbnail.thumbnail(size, Image.ANTIALIAS)
			yield thumbnail

	frames = thumbnails(frames)

	output_image = next(frames)
	output_image.info = im.info

	output_image.save(gif_file_name, save_all=True, append_images=list(frames))



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
	print(stored_dict.keys())



	output_gifs = set()

	for gif_name in stored_dict.keys():

		distance = get_dist(rgb, stored_dict[gif_name]['avg_rgb'])
		if len(output_gifs) < num_gifs:
			output_gifs.add((gif_name, distance))
		else:

			worst_gif = max(output_gifs, key = lambda tup: tup[1])

			if worst_gif[1] > distance:
				output_gifs.remove(worst_gif)
				output_gifs.add((gif_name, distance))


	print(output_gifs)

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

stored_dict = {}


search_words_dict = {
	'green': ['green', 'forest', 'avocado', 'cactus', 'kiwi-fruit', 'ever-green-tree', 'st-patricks-day', 'hulk', 'oscar-the-grouch'],
	'red': ['red', 'raspberry-fruit', 'mars-planet', 'lobster', 'fire', 'canadian-flag', 'red-pirate', 'tomato-fruit', 'ladybug', 'mr-krabs'],
	'blue': ['blue', 'sky', 'ocean', 'deep-ocean', 'blue-man-group', 'beach', 'water-ocean', 'stitch', 'cookie-monster', 'frozen', 'squidward'],
	'yellow': ['yellow', 'minion', 'lemon', 'spongebob', 'orange', 'sun', 'gold', 'simpsons', 'cheese', 'corn'],
	'brown': ['brown', 'dirt', 'desert', 'shit', 'brown-eyes', 'chocolate', 'chocolate-cake', 'coffee', 'monkey'],
	'black': ['outer-space', 'night', 'darkness', 'darth-vader', 'solar-system', 'black-oil', 'black-and-white', 'black-and-white-film'],
	'whhite': ['white-canvas', 'samoyed', 'wedding-dress', 'snow', 'white-cloud',  'white-paint', 'styrofoam', 'vanilla-ice-cream'],
	'purple': ['purple', 'eggplant', 'purple-back']
}



for color in search_words_dict.keys():
	for word in search_words_dict[color]:
		stored_dict = process_gifs(word, "fixed_height", stored_dict)


write_to_text(stored_dict)



# with open('gif_data.json') as f:
# 	stored_dict_from_txt = json.load(f)

# get_nearest_gifs((125,125,125), stored_dict_from_txt, 10)
