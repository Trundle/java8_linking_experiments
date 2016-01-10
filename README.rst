============================================
Type-safe URI linking in Java 8 with Ratpack
============================================

Demonstration of how to create links to handlers in a type-safe way.

A working implementation is done for the `Ratpack web framework
<https://ratpack.io/>`_, though the general idea should work for other web
frameworks as well.

``RootResourceHandler`` demonstrates how the type-safe linking can be used. The
root ressource links to the greeting ressource. The method that
creates the links (``Router#linkTo``) requires the same parameters as the actual
handler method ``RootRessourceHandler#greet(Context, String)`` (except for the
``Context`` parameter, which every handler receives).

The current implementation currently limits the path parameters to strings.
Additionally, only one path parameter is supported at max. Note that those are
limitations of the implementation, not of the general approach.


License
=======

MIT/Expat. See ``LICENSE`` for details.
