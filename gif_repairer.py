import os
from PIL import Image, ImageSequence





def repair(path):
	im = Image.open(path)


	if is_partial(im):
		print('REPAIRING:!', path)
		frames = ImageSequence.Iterator(im)
		last_frame = im.convert('RGBA')
		frames = []
		p = im.getpalette()
		try:
			while True:
				if not im.getpalette():
					im.putpalette(p)
				# if not im.getpalette():
				# 	im.putpalette(p)
				
				'''
				If the GIF uses local colour tables, each frame will have its own palette.
				If not, we need to apply the global palette to the new frame.
				'''
				new_frame = Image.new('RGBA', im.size)
				'''
				Is this file a "partial"-mode GIF where frames update a region of a different size to the entire image?
				If so, we need to construct the new frame by pasting it on top of the preceding frames.
				'''
				new_frame.paste(last_frame)
				
				new_frame.paste(im, im.convert('RGBA'))
				# new_frame.show()
				frames.append(new_frame)
				last_frame = new_frame
				im.seek(im.tell() + 1)
		except EOFError:
			pass

	frames[0].save(path, save_all=True, append_images=list(frames[1:]), loop=0)


def is_partial(im):
	try:
		while True:
			if im.tile:
				tile = im.tile[0]
				# print('tile',tile)
				# print('size',im.size)
				update_region = tile[1]
				if update_region != (0,0)+(im.size):
					print("upadate region", update_region)
					return True
			im.seek(im.tell() + 1)
	except EOFError:
		return False
	return False

if __name__ == "__main__":
	for file in os.listdir('./'):
		if (file.endswith(".gif")):
			print(file)
			repair(file)