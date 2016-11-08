
// Global one

struct MyStruct{
};
union MyUnion{
};
class MyClass{
};

// Inside namespace
namespace ns1{
	struct MyStruct2{
	};
	union MyUnion2{
	};
	class MyClass2{
	};
}

// Nested
struct MyStruct3{
	struct MyStruct4{};
};
union MyUnion3{
	union MyUnion4{};
};
class MyClass3{
	class MyClass4{};
};  