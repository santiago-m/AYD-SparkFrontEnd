package trivia;

public class Pair<S, T> {
	S first;
	T second;

	public Pair(S newFirst, T newSecond) {
		first = newFirst;
		second = newSecond;
	}

	public S first() {
		return first;
	}

	public T second() {
		return second;
	}
}