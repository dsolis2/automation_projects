# This is the main driver script

from functionality import *
from othermodule import *
from othermodule.submodule import fourth

# Second level directory call

print(add(10, 20))
print(sub(20, 10))
print(mul(10, 20))
print(div(56, 8))

# Third level directory call
second.myfunction()
third.another_function()

# Fouth level directory call
fourth.last()