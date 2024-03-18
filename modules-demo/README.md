This is a demo to show how the import statement works 2 directory level deep 

├── README.md
├── __pycache__
│   └── functionality.cpython-312.pyc
├── functionality.py
├── main.py
└── othermodule
    ├── __init__.py
    ├── __pycache__
    │   ├── __init__.cpython-312.pyc
    │   ├── second.cpython-312.pyc
    │   └── third.cpython-312.pyc
    ├── second.py
    ├── submodule
    │   ├── __pycache__
    │   │   └── fourth.cpython-312.pyc
    │   └── fourth.py
    └── third.py