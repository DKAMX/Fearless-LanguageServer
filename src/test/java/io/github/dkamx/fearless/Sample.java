package io.github.dkamx.fearless;

public class Sample {

  public static final String HELLO = """
      package test
      alias base.Main as Main,
      alias base.caps.UnrestrictedIO as UnrestrictedIO,
      //alias base.caps.UnrestrictedIO as UnrestrictedIO,//should it be like this?
      Test:Main {sys -> UnrestrictedIO#sys.println("Hello, World!")}
      //prints Hello, World!
      """;

  public static final String HELLO_DEBUG = """
      package test
      Test:Main {sys -> base.Debug.println("Hello, World!")}//OK
      """;

  public static final String HELLO_BLOCK1 = """
      package test
      alias base.Main as Main,
      alias base.Block as Block,
      alias base.caps.UnrestrictedIO as UnrestrictedIO,
      Test:Main {sys -> Block#
        .let io = {UnrestrictedIO#sys}
        .return {io.println("Hello, World!")}
        }
      //prints Hello, World!
      """;

  public static final String HELLO_BLOCK2 = """
      package test
      alias base.Main as Main,
      alias base.Block as Block,
      alias base.caps.UnrestrictedIO as UnrestrictedIO,
      alias base.Void as Void,
      Test:Main {sys -> Block#
        .let io = {UnrestrictedIO#sys}
        .do {io.println("Hello, World!")}
        .return {Void}
        }
      //prints Hello, World!
      """;

  public static final String HELLO_BLOCK3 = """
      package test
      alias base.Main as Main,
      alias base.Block as Block,
      alias base.caps.UnrestrictedIO as UnrestrictedIO,
      Test:Main {sys -> Block#
            .let io = {UnrestrictedIO#sys}
            .do {io.println("Hello, World!")}
            .done
            }
          //prints Hello, World!
      """;

  public static final String FRUIT = """
      package test
      alias base.Str as Str,
      alias base.Nat as Nat,
      Fruit: {
        .name: Str,
        .value: Nat,
        }
      Apple: Fruit {
        .name -> "Apple",
        .value -> 1,
        }
      FruitMachine: {
        .detect(fruit: Fruit): Str -> fruit.name,
        .compare(fruit1: Fruit, fruit2: Fruit): Nat,
        }
      """;

  public static final String HELLO_ERR = """
      package test
      alias base.Main as Main,
      alias base.caps.UnrestrictedIO as , // error
      Test:Main {sys -> UnrestrictedIO#sys.println("Hello, World!")}
      //prints Hello, World!
      """;

  public static final String LIST = """
      package test
      alias base.Main as Main,
      alias base.Block as Block,
      alias base.caps.UnrestrictedIO as UnrestrictedIO,
      alias base.List as List,
      Test:Main {sys -> Block#
        .let io = {UnrestrictedIO#sys}
        .let data = {List#(1, 2, 3, 4)}
        .do {data.flow.map{e -> e}}
        .return {io.println("Hello, World!")}
      }
      //prints Hello, World!
      """;

  public static final String LIST_CMPL = """
      package test
      alias base.Main as Main,
      alias base.Block as Block,
      alias base.caps.UnrestrictedIO as UnrestrictedIO,
      alias base.List as List,
      Test:Main {sys -> Block#
        .let io = {UnrestrictedIO#sys}
        .let data = {List#(1, 2, 3, 4)}
        .do {data.flow.map{e -> base.Id.id(e)}}
        .return {io.println("Hello, World!")}
      }
      //prints Hello, World!
      """;

  public static final String LIST2 = """
      package test
      alias base.Main as Main,
      alias base.Block as Block,
      alias base.caps.UnrestrictedIO as UnrestrictedIO,
      alias base.List as List,
      Test:Main {sys -> Block#
        .let io = {UnrestrictedIO#sys}
        .let data = {List#(1, 2, 3, 4)}
        .do {data.flow.map{e -> e.str}}
        .return {io.println("Hello, World!")}
      }
      //prints Hello, World!
      """;

  public static final String LIST2_CMPL = """
      package test
      alias base.Main as Main,
      alias base.Block as Block,
      alias base.caps.UnrestrictedIO as UnrestrictedIO,
      alias base.List as List,
      Test:Main {sys -> Block#
        .let io = {UnrestrictedIO#sys}
        .let data = {List#(1, 2, 3, 4)}
        .do {data.flow.map{e -> base.Id.id(e.str)}}
        .return {io.println("Hello, World!")}
      }
      //prints Hello, World!
      """;

  public static final String LIST_CMPL_BLOCK = """
      package test
      alias base.Main as Main,
      alias base.Block as Block,
      alias base.caps.UnrestrictedIO as UnrestrictedIO,
      alias base.List as List,
      Test:Main {sys -> Id.id(Block#
        .let io = {UnrestrictedIO#sys}
        .let data = {List#(1, 2, 3, 4)}
        .do {data.flow.map{e -> e}})
        .return {io.println("Hello, World!")}
      }
      //prints Hello, World!
      """;
}