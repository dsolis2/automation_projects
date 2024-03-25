import sys
sys.path.append("../extern")

#from extern.my_package.classes import MyClass

import extern.my_package.classes as test 

test.MyClass("a")
print(test)