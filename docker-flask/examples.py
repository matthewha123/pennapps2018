#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Created on 17:54:54 2016-10-21

@author: heshenghuan (heshenghuan@sina.com)
http://github.com/heshenghuan
"""


import kdtree
import knn
import Distance as ds
import math


def example_kdtree():
    # An example of how to use kdtree
    print "*" * 60
    print "*" * 15, "An Example of kdtree's Usage", "*" * 15
    print "*" * 60
    point = [(2, 3), (5, 4), (9, 6), (4, 7), (8, 1), (7, 2), (8, 8)]
    point1 = []
    for i in point:
        point1.append({1: i[0], 2: i[1]})
    print "point list"
    print point1
    # Create a kdtree
    root = kdtree.create(point1, dimensions=2)
    # Visualize the kdtree
    print "visualize the kd-tree: "
    kdtree.visualize(root)
    # Search for k-nearsest neighbor by given p-Minkowski distance
    f = ds.EuclideanDistance
    ans = root.search_knn(point={1: 7, 2: 3}, k=10, dist=f)
    print "The 3 nearest nodes to point (7, 3) are:"
    print ans
    print "The nearest node to the point is:"
    print ans[0][0].data


def example_knn():
    # An example of how to use knn
    print "*" * 60
    print "*" * 16, "An Example of knn's Usage", "*" * 17
    print "*" * 60
    data1 = [(3, 5), (2, 3), (5, 4), (9, 6), (4, 7), (8, 1), (7, 2), (8, 8)]
    data = []
    for i in data1:
        data.append({0: i[0], 1: i[1]})
    label = [1, 1, 1, 0, 1, 0, 1, 0]
    m = knn.KNN(data, label, dimensions=2)
    print "Samples:", m.train_data
    print "\nLabel prb:", m.class_prb
    # print m.decision()
    print "\n\nvisualize the kd-tree: "
    m.visualize_kdtree()
    f = ds.EuclideanDistance
    print "the label of point", {0: 9, 1: 9}, "is",
    print m.classify(point={0: 9, 1: 9}, k=3, dist=f, prbout=1)
    print "the label of point", {0: 2, 1: 8}, "is",
    print m.classify(point={0: 2, 1: 8}, k=3, dist=f, prbout=1)
    knn.saveknn(m, 'testknn.pkl')

    # Pickle test
    print "*" * 60
    print "Load knn model from file: 'testknn.pkl'"
    n = knn.loadknn('testknn.pkl')
    print "Samples:", n.train_data
    print "\nLabel prb:", n.class_prb
    # print n.decision()
    print "\n\nvisualize the kd-tree: "
    n.visualize_kdtree()


if __name__ == "__main__":
    example_kdtree()
    example_knn()
