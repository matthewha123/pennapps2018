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





def downloadGifs(search_query, gif_format_to_use):
	data = json.loads(urllib.request.urlopen("http://api.giphy.com/v1/gifs/search?q="+search_query+"&api_key="+giphyKey+"&limit=25").read())
	gifs = data['data']

	for gif in gifs:
		# print(gif['images'][gif_format_to_use])

		gif_file_name = gif['slug']+".gif"
		urllib.request.urlretrieve(gif['images'][gif_format_to_use]['url'], gif['slug']+".gif")
		
		iterate_and_averagecolor(gif_file_name)


def iterate_and_averagecolor(gif_file_name):

	#schema: filename: {rgb: (num, num, num), }

	gif_img = Image.open(gif_file_name)
	

	n_frames = gif_img.n_frames
	i = 0

	fourth = n_frames // 4

	intervals = [fourth * i for i in range(1,5)]
	print(intervals)
	while(i<gif_img.n_frames):
		i+= 1
		gif_img.seek(i)
		if(i in intervals):
			to_rgb = gif_img.convert(mode="RGB")
			average_rgb = [mean(to_rgb.getdata(band)) for band in range(3)]
			print(gif_file_name, ":", average_rgb)
			# gif_img.show()

def get_average_color(image):
  """
  Given PIL Image, return average value of color as (r, g, b)
  """

  # no. of pixels in image
  npixels = image.size[0]*image.size[1]
  # get colors as [(cnt1, (r1, g1, b1)), ...]
  cols = image.getcolors(npixels)
  # get [(c1*r1, c1*g1, c1*g2),...]
  sumRGB = [(x[0]*x[1][0], x[0]*x[1][1], x[0]*x[1][2]) for x in cols] 
  # calculate (sum(ci*ri)/np, sum(ci*gi)/np, sum(ci*bi)/np)
  # the zip gives us [(c1*r1, c2*r2, ..), (c1*g1, c1*g2,...)...]
  avg = tuple([sum(x)/npixels for x in zip(*sumRGB)])
  return avg


downloadGifs("green", "fixed_height")