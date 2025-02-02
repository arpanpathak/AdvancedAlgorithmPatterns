# Problem Solutions Repository

This repository contains solutions to various algorithmic problems, organized by categories such as arrays, backtracking, binary search, graphs, and more. Each problem is solved in Kotlin, and the solutions are structured in a way that makes it easy to navigate and understand.

## Purpose

The purpose of this repository is to provide a comprehensive collection of algorithmic problem solutions that can be used for learning, reference, or interview preparation. The problems are categorized by topic, and each solution is implemented in Kotlin.

## How to Generate an Updated File List

To generate an updated list of all files in this repository, you can use the following shell command:

```bash
find . -type f | sort > all_problems.txt
```

This command will recursively find all files in the repository, sort them, and save the list to a file named `all_problems.txt`.

## Repository Structure

Below is a tree-like structure of the repository with links to the files:

```
.
├── array
│   ├── Combinatorics
│   │   ├── Combinations.kt
│   │   ├── NextPermutation.kt
│   │   ├── Permutation_II.kt
│   │   ├── Permutation_II_Backtracking.kt
│   │   ├── Permutations.kt
│   │   └── Subsets.kt
│   ├── DiagonalTraverse.kt
│   ├── MergeIntervals.kt
│   ├── MergeSortedArray.kt
│   ├── MissingRanges.kt
│   ├── MoveZeroes.kt
│   ├── RemoveElement.kt
│   ├── RotateImage.kt
│   ├── ShortestPathInBinaryMatrix.kt
│   ├── SpiralMatrix.kt
│   ├── ToeplitzMatrix.kt
│   ├── TransposeMatrix.kt
│   ├── backtracking
│   │   ├── CombinationSum.kt
│   │   ├── CombinationSum3.kt
│   │   └── CombinationSum_II.kt
│   ├── dfs
│   │   └── NestedListWeightedSum.kt
│   ├── dp
│   │   ├── CoinChange.kt
│   │   ├── CoinChange_II.kt
│   │   ├── CoinChange_II_BottomUp.kt
│   │   ├── HouseRobber.kt
│   │   ├── HouseRobber_II.kt
│   │   ├── LongestCommonSubarray.kt
│   │   ├── LongestIncreasingSubsequence.kt
│   │   ├── MaximumSumSubArray.kt
│   │   ├── MinCostClimbingStaris.kt
│   │   └── TargetSum.kt
│   ├── greedy
│   │   ├── CanPlaceFlowers.kt
│   │   ├── ContainerWithMostWater.kt
│   │   ├── IncreasingTripletSequence.kt
│   │   ├── KItemsWithMaximumSum.kt
│   │   ├── MaximumDistanceInArray.kt
│   │   ├── MaximumSwap.kt
│   │   ├── MergeOverlappingIntervals.kt
│   │   └── NonOverlappingIntervals.kt
│   ├── hashtable
│   │   ├── EqualRowAndColumnPairs.kt
│   │   ├── FindDifferenceOfTwoArrays.kt
│   │   ├── FindMissingPositive.kt
│   │   ├── LongestConsecutiveSequence.kt
│   │   ├── MaxNUmWithKSumPairs.kt
│   │   ├── RankTransformOfAnArray.kt
│   │   ├── UniqueNumberOfOccurences.kt
│   │   └── ValidSudoku.kt
│   ├── prefixsum
│   │   ├── ContiguousArray.kt
│   │   ├── ContinuousSubarraySum.kt
│   │   ├── FIndTheHighestAltitute.kt
│   │   ├── FindPivotIndex.kt
│   │   ├── Minimum NumberofOperationstoMoveAllBallstoEachBox.kt
│   │   ├── NumberOfZeroFilledSubArrays.kt
│   │   ├── SubArraySumEqualsToK.kt
│   │   └── SubArraySumsDivisibleByK.kt
│   ├── random
│   │   └── RandomPickIndex.kt
│   ├── sorting
│   │   └── SortColors.kt
│   └── twopointer
│       ├── IntervalListIntersection.kt
│       ├── RemoveDuplicateElementsFromSortedArray.kt
│       ├── RemoveDuplicateElementsFromSortedArray_II.kt
│       ├── RotateArray.kt
│       ├── ThreeSum.kt
│       ├── TrappingRainWater.kt
│       └── TwoSum_II.kt
├── autopilot
│   └── H1bAutoPilotStressAnxietyAlgorithm.kt
├── backtracking
│   ├── ExpressionAndAddOperators.kt
│   ├── ExpressionAndAddOperatorsOptimized.kt
│   ├── NQueen.kt
│   └── NQueen_II.kt
├── binarysearch
│   ├── ApartmentHunting.kt
│   ├── CapacityToShipPackageWithinDDays.kt
│   ├── FindFirstAndLastPosition.kt
│   ├── FindKClosestElements.kt
│   ├── FindPeakElement.kt
│   ├── FindPeakElementBetterSolution.kt
│   ├── FirstBadVersion.kt
│   ├── GuessNumberHigherOrLower.kt
│   ├── KThMissingPositiveNumber.kt
│   ├── KokoEatingBanana.kt
│   ├── MedianOfTwoSortedARrays.kt
│   ├── RandomPickWithWeight.kt
│   ├── SearchInRotatedArray_II.kt
│   ├── SearchInRotatedSortedArray.kt
│   ├── SearchInsertionPosition.kt
│   └── ValleyElement.kt
├── bitset
│   ├── FirstLetterToAppearTwice.kt
│   ├── MaximumXorOfTwoNumsInArray.kt
│   ├── NumberOfOneBits.kt
│   └── SingleNumber3.kt
├── cache
│   ├── LFUCache.kt
│   ├── LRUCache.kt
│   └── LRUCacheLinkedList.kt
├── commons
│   ├── APIEndPoints.kt
│   ├── AlpacaWebSocketFactory.kt
│   └── FileWriter.kt
├── disjointset
│   ├── AccountMerge.kt
│   └── UnionFind.kt
├── facebook
│   ├── FindMinimumTicketPrice.kt
│   └── SecondGreatestNumber.kt
├── graph
│   ├── CloneGraph.kt
│   ├── DiameterOfBinaryTree.kt
│   ├── HouseRobber3.kt
│   ├── articulation_point
│   │   ├── CriticalConnectionsInANetwork.kt
│   │   └── FindArticulationPoints.kt
│   ├── bst
│   │   ├── BinarySearchTreeToGreaterSumTree.kt
│   │   └── RangeSumOfBST.kt
│   ├── cycle
│   │   ├── CourseSchedule.kt
│   │   └── CourseSchedule_II.kt
│   ├── dp
│   │   └── CheapestFlightsWithinKStops.kt
│   ├── greedy
│   │   ├── CheapestFlightWithKStops.kt
│   │   ├── CheapestFlightsWithKStops.kt
│   │   └── DonaldTrumpAlgorithm.kt
│   ├── mst
│   │   └── PrimsAlgorithm.kt
│   └── topological_sort
│       ├── AlienDictionary.kt
│       ├── CourseSchedule_II.kt
│       └── CourseSchedule_II_BFS.kt
├── greedy
│   ├── CarFleet.kt
│   ├── JumpGame.kt
│   ├── JumpGame_II.kt
│   ├── MInimumCostHomecomingOfARobot.kt
│   ├── MeetingRooms.kt
│   └── MeetingRooms_II.kt
├── grid
│   ├── MakingALargeIsland.kt
│   ├── MaxAreaOfIsland.kt
│   ├── PacificAtlanticWaterFlow.kt
│   ├── RottingOranges.kt
│   ├── ShortestBridge.kt
│   ├── dynamic_programming
│   │   ├── CherryPickup.kt
│   │   ├── Test.kt
│   │   ├── UniquePaths_I.kt
│   │   └── UniquePaths_II.kt
│   ├── histogram
│   │   └── MaximalRectangle.kt
│   └── search
│       └── WordSearch_II.kt
├── hashtable
│   ├── IntegerToRoman.kt
│   ├── RomanToInteger.kt
│   └── WorlBreak_I_DP.kt
├── heap
│   ├── DualBalancedHeap.kt
│   ├── FindKClosestElements.kt
│   ├── FindScoreOfAnArrayAfterMarkingAllElements.kt
│   ├── MedianFromRunningStream.kt
│   ├── SlidingWindowMedian.kt
│   └── TopKFrequentElements.kt
├── linkedlist
│   ├── AddTwoNumbers.kt
│   ├── CopyLinkedListWithRandomPointer.kt
│   ├── DeleteMiddleNodeOfLinkedList.kt
│   ├── InsertIntoASortedCircularLinkedList.kt
│   ├── LinkedListCycle.kt
│   ├── MergeKSortedList.kt
│   ├── MergeKSortedListIterative.kt
│   ├── MergeTwoSortedLIst.kt
│   ├── OddEvenLinkedList.kt
│   ├── OddOrEvenLinkedList.kt
│   ├── RemoveNthNodeFromEndOfList.kt
│   ├── ReverseLinkedList.kt
│   └── ReverseNodesInKGroups.kt
├── math
│   ├── AddStrings.kt
│   ├── DetectSquares.kt
│   ├── MultiplyStrings.kt
│   ├── ReverseInteger.kt
│   ├── StringtoIntegerAtoi.kt
│   ├── geometry
│   │   └── ErectTheFence_ConvexHull.kt
│   ├── pow.kt
│   └── stack
│       ├── BasicCalculator.kt
│       ├── BasicCalculator_I.kt
│       ├── BasicCalculator_II.kt
│       └── BasicCalculator_II_ShortCode.kt
├── microsoft
│   ├── Demo.kt
│   ├── NonNegativeSum.java
│   ├── Toast.kt
│   └── ValidTime.kt
├── ml
│   └── classical
│       └── tree
│           └── core
│               └── DecisionTree.kt
├── numbers
│   └── PalindromeNumber.kt
├── probability
│   ├── InsertDeleteGetRandomAtO1.kt
│   ├── LinkedListRandomNode.kt
│   └── ReservoirSampling.kt
├── queueu
│   └── dequeue
│       └── NumberOfRecentCalls.kt
├── quicksort
│   ├── KClosestPointsToOrigin.kt
│   ├── KThLargestElementInArray.kt
│   └── TopKFrequentElements.kt
├── real_word_projects
│   ├── HttpApiCall.kt
│   ├── InterfaceExample.kt
│   ├── ParallelFibonacci.kt
│   ├── SystemInterviewHack.kt
│   ├── TradingAPICallExample.kt
│   ├── database
│   │   └── AiTest.kt
│   ├── dynamic_programming
│   │   └── DuckworthLewisStern.kt
│   ├── json
│   │   └── JsonExample.kt
│   ├── parallel_algorithms
│   │   ├── FindMaxInArray.kt
│   │   └── ParallelMatrixMultiplication.kt
│   └── trading
│       ├── AlpacaSDKExamples.kt
│       ├── RealtimeMarketDataStreaming.kt
│       ├── system_design_plantuml.txt
│       └── trading_data.json
├── simulation
│   ├── CountCollisionsOnARoad.kt
│   ├── Racecar.kt
│   └── TextJustification.kt
├── sliding_window
│   ├── LongestSubArraysOfOneAfterDeletingOneElement.kt
│   ├── LongestSubstringWithoutRepeatingCharacter.kt
│   ├── MaxConsecutiveOnes_III.kt
│   ├── MaximumAverageSubarray_I.kt
│   ├── MaximumErasureValue.kt
│   ├── MaximumSumOfDistinctSubarraysWithLengthK.kt
│   ├── MinimumSizeSubarraySum.kt
│   ├── PartitionLabels.kt
│   └── SlidingWindowMaximum.kt
├── speed_dating
│   └── ComputerScienceEngineerDating.kt
├── stack
│   ├── AestroidCollisions.kt
│   ├── BuildingsWithAnOceanView.kt
│   ├── DailyTemperatures.kt
│   ├── EvaluateReversePolishNotation.kt
│   ├── LargestRectangleInHistogram.kt
│   ├── MinimumAddtoMakeParenthesesValid.kt
│   ├── MinimumRemoveToMakeValidParentheses.kt
│   ├── NextGreaterElement_I.kt
│   ├── OnlineStockSpan.kt
│   ├── RemoveStarsFromString.kt
│   └── ValidParentheses.kt
├── stock_market
│   ├── dp
│   │   ├── BestTimeToBuyAndSellStock.kt
│   │   ├── BestTimeToBuyAndSellStockWithCooldown.kt
│   │   ├── BestTimeToBuyAndSellStockWithTransactionFee.kt
│   │   └── BestTimeToBuyAndSellStock_III.kt
│   └── greedy
│       └── BestTimeToBuyAndSellStock_II.kt
├── stream
│   └── MovingAverageOfARunningStream.kt
├── string
│   ├── CountAndSay.kt
│   ├── CountWordsWithAGivenPrefix.kt
│   ├── GoatLatin.kt
│   ├── GreatestCommonDivisorOfStrings.kt
│   ├── IsSubsequence.kt
│   ├── MaximumValueOfAStringIsAnArray.kt
│   ├── MergeStringAlternatively.kt
│   ├── ReverseVowelOfString.kt
│   ├── ReverseWordsInString.kt
│   ├── StringCompression.kt
│   ├── ValidNumber.kt
│   ├── ValidPalindrome.kt
│   ├── ValidPalindrome_II.kt
│   ├── ValidWordAbbreviation.kt
│   ├── backtracking
│   │   └── GenerateParantheses.kt
│   ├── dynamic_programming
│   │   ├── EditDistance.kt
│   │   ├── LongestCommonSubsequence.kt
│   │   ├── LongestCommonSubstring.kt
│   │   ├── RegularExpressionMatching.kt
│   │   └── ValidPalindrome_III.kt
│   ├── hashtable
│   │   ├── DetermineIfStringsAreClose.kt
│   │   ├── GroupShiftedStrings.kt
│   │   └── UniqueLength3PalindromicSubsequence.kt
│   ├── pattern_matching
│   │   └── FindTheIndexofTheFirstOccurrenceIna String.kt
│   ├── sliding_window
│   │   ├── MaximumNumberofVowelsinSubstringofGivenLength.kt
│   │   └── MinimumWindowSubstring.kt
│   └── stack
│       ├── DecodeString.kt
│       ├── RemoveAllAdjacentDuplicatesInString.kt
│       └── SimplifyPath.kt
├── trading
│   └── trading_data.json
├── tree
│   ├── AllNodesDistanceKinBinaryTree.kt
│   ├── BinaryTreeLevelOrderTraversal.kt
│   ├── BinaryTreeRightSideView.kt
│   ├── BinaryTreeVerticalOrderTraversal.kt
│   ├── BinaryTreeVerticalOrderTraversal_WithoutSorting.kt
│   ├── BinaryTreeZigZagLevelOrderTraversal.kt
│   ├── BoundaryOfBinaryTree.kt
│   ├── ConstructBinaryTreeFromPreorderAndInOrderTraversal.kt
│   ├── ConstructBinaryTreeFromString.kt
│   ├── CountGoodNodeInBInaryTree.kt
│   ├── CountNodeEqualsAverage.kt
│   ├── LeafSimilar.kt
│   ├── LowestCommonAncestor.kt
│   ├── LowestCommonAncestor_III.kt
│   ├── MaximumDepthOfBinaryTree.kt
│   ├── MaximumSumBSTInBinaryTree.kt
│   ├── MaximumWidthOfBinaryTree.kt
│   ├── MinimumTimeToCollectAllApplesInATree.kt
│   ├── PathSum.kt
│   ├── PathSumIII.kt
│   ├── PathSum_II.kt
│   ├── PopulatingNextRightPointerInEachNode.kt
│   ├── StepByStepDirectionsFromANodeToAnother.kt
│   ├── bfs
│   │   ├── AverageOfLevelsInBinaryTree.kt
│   │   ├── BinaryTreeLevelOrderTraversal_II.kt
│   │   ├── CheckCompletenessOfBinaryTree.kt
│   │   └── FindLargestValueInEachTreeRow.kt
│   ├── bst
│   │   ├── BSTIterator.kt
│   │   ├── ClosestBinarySearchTreeValue.kt
│   │   ├── ConvertBInarySearchTreeToSortedDoublyLinkedList.kt
│   │   ├── DeleteNodeinABST.kt
│   │   ├── LongestIncreasingSubsequence.kt
│   │   └── SkylineProblem.kt
│   └── mst
│       ├── MinCostToConnectAllPointsKruskal.kt
│       └── MinCostToConnectAllPointsPrims.kt
└── trie
    ├── AutoCompleteSystem.kt
    ├── AutoCompleteSystemWithHeap.kt
    ├── EqualRowAndColumnPairs.kt
    ├── LongestCommonPrefix.kt
    ├── SearchSuggestionSystem.kt
    └── WordBreak_I.kt
```

## Contributing

If you would like to contribute to this repository, feel free to submit a pull request. Please ensure that your solutions are well-documented and follow the existing structure.

## License

This repository is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

---

Happy coding! 🚀