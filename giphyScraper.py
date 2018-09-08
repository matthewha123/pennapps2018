#giphy api key

giphyKey = "TSIIIOcIx5kqFH1hQ4LIaLo4zvId3SBk"

import urllib,json
import urllib.request

from PIL import Image
from statistics import mean

import numpy as np

data = json.loads(urllib.request.urlopen("http://api.giphy.com/v1/gifs/search?q=green&api_key="+giphyKey+"&limit=25").read())
formatted_data = json.dumps(data, sort_keys=True, indent=4)


gifs = data['data']





def downloadGifs(search_query, gif_format_to_use, stored_dict):
	data = json.loads(urllib.request.urlopen("http://api.giphy.com/v1/gifs/search?q="+search_query+"&api_key="+giphyKey+"&limit=25").read())
	gifs = data['data']

	for gif in gifs:
		# print(gif['images'][gif_format_to_use])

		gif_file_name = gif['slug']+".gif"
		gif_url = gif['images'][gif_format_to_use]['url']
		urllib.request.urlretrieve(gif_url, gif_file_name)
		avg_rgb = iterate_and_averagecolor(gif_file_name)
		print('Final: ', avg_rgb)
		stored_dict[gif_file_name] = {"url":gif_url, "avg_rgb": avg_rgb}
		print(stored_dict)
		write_to_text(stored_dict)

def write_to_text(dictionary):
	with open('gif_data.txt', 'a') as gif_data:
		json.dump(dictionary, gif_data, indent = 4)

def iterate_and_averagecolor(gif_file_name):

	#schema: filename: {rgb: (num, num, num), }

	gif_img = Image.open(gif_file_name)
	

	n_frames = gif_img.n_frames - 1
	i = 0

	fourth = n_frames // 4

	intervals = [fourth * i for i in range(1,5)]
	print(n_frames)
	print(intervals)



	final_average = [0,0,0]
	while(i<gif_img.n_frames):
		gif_img.seek(i)
		if(i in intervals):
			to_rgb = gif_img.convert(mode="RGB")
			average_rgb = getAverageRGBN(to_rgb)
			final_average = [final_average[i] + average_rgb[i] for i in range(3)]
		i+= 1
	return [final_average[i]/len(intervals) for i in range(3)]


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



stored_dict = {}
stored_dict = downloadGifs("green", "fixed_height", stored_dict)